import time
import datetime
import urllib

import pandas
from pytz import timezone
import numpy
import pyopencl as cl
import pyopencl.array as cl_array
import json
from kafka import KafkaProducer


def google_finance_data_reader(symbol, interval_seconds, num_days):
    url_string = "http://www.google.com/finance/getprices?q={symbol}".format(symbol=symbol.upper())
    url_string += "&i={interval_seconds}&p={num_days}d&f=d,o,h,l,c,v".format(interval_seconds=interval_seconds,
                                                                             num_days=num_days)
    # print(url_string)
    page = urllib.urlopen(url_string)
    df = pandas.read_csv(page, skiprows=7, sep=',', names=['DATE', 'CLOSE', 'HIGH', 'LOW', 'OPEN', 'VOLUME'])
    b_date_round = df['DATE'].map(lambda dt: dt[0] == 'a')
    date_round = df[b_date_round]['DATE'].map(lambda dt: int(dt[1:]))
    df['DATE2'] = date_round
    df['DATE2'] = df['DATE2'].fillna(method='ffill')
    df['DATE3'] = df[~b_date_round]['DATE'].astype(int) * interval_seconds
    df['DATE3'] = df['DATE3'].fillna(0)
    df['DATE4'] = df['DATE2'] + df['DATE3']
    df['DATE4'] = df['DATE4'].map(lambda s: datetime.datetime.fromtimestamp(int(s), timezone('US/Eastern')))
    del df['DATE']
    del df['DATE2']
    del df['DATE3']
    df = df.set_index('DATE4', verify_integrity=True)
    df.index.name = 'DATE'
    return df


def pyopencl_mean(x_gpu_in):
    return cl_array.sum(x_gpu_in) / float(x_gpu_in.size)


def pyopencl_stddev(x_gpu_in):
    mean = pyopencl_mean(x_gpu_in).get()
    element_wise__mean_diff__sq = (x_gpu_in - mean) ** 2
    return pyopencl_mean(element_wise__mean_diff__sq) ** 0.5


def publish_market_predictions(predictions):
    msg = json.dumps(predictions)
    print msg
    producer.send("test-ks-predictions", value=msg)
    producer.flush()


ctx = cl.create_some_context()
queue = cl.CommandQueue(ctx)

producer = KafkaProducer(bootstrap_servers='localhost:9092', value_serializer=str.encode)

interval_seconds = 60
num_days = 1
symbols = ["AMZN", "IBM", "MSFT", "CVC", "V", "KO", "BRK-B", "AA", "GS", "SBUX", "QCOM"]
# symbols = ["BAC", "C", "IBM", "AAPL", "GE", "T", "MCD", "NKE", "TWTR", "TSLA"]

while True:
    predictions = {}

    for symbol in symbols:
        data_frame = google_finance_data_reader(symbol.replace("-", "."), interval_seconds, num_days)
        prices_latest = data_frame['CLOSE'].tail(30).as_matrix()
        prices = cl_array.to_device(queue, prices_latest)
        predictions[symbol] = float(pyopencl_stddev(prices).get())

    publish_market_predictions({"Predictions": predictions})

    time.sleep(60)  # Delay for 10 seconds

