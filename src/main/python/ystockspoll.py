import time
from urllib import urlopen
import json
from kafka import KafkaProducer

companies = ["AMZN", "IBM", "MSFT", "CVC", "V", "KO", "BRK-B", "AA", "GS", "SBUX", "QCOM"]
url = 'http://finance.yahoo.com/d/quotes.csv?s=' + ",".join(companies) + '&f=l1'
producer = KafkaProducer(bootstrap_servers='localhost:9092', value_serializer=str.encode)


def get_market_data():
    content = urlopen(url).read().decode().strip()
    prices = map(lambda ps: float(ps), content.split(',')[0].splitlines())
    return dict(zip(companies, prices))


def publish_market_data(market_data):
    msg = json.dumps(market_data)
    print msg
    producer.send("test-ks-prices", value=msg)
    producer.flush()


while True:
    market_data = get_market_data()
    publish_market_data({"MarketData": market_data})
    time.sleep(10)  # Delay for 10 seconds
