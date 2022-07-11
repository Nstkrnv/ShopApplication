#import numpy as np
#import pandas as pd
from statsmodels.tsa.arima.model import ARIMA
#from sklearn.metrics import mean_squared_error as mse
#from sklearn.metrics import mean_absolute_error as mae
import datetime
import warnings
import sqlite3
import sys

if len(sys.argv) < 2:
    exit(-1)
id = sys.argv[1]

#warnings.filterwarnings("ignore")

conn = sqlite3.connect(r'C:/Users/Anastasia/Databaselogistics/dtt.db')
cur = conn.cursor()

cur.execute("DELETE FROM prediction")

cur.execute("SELECT дата,\n" +
                    "      count(id)\n" +
                    "  FROM outcome\n" +
                    "  where id = " + id + "\n" +
                    "  Group by дата" +
                    "   order by дата")
all_results = cur.fetchall()

arr = []

prefDate = all_results[0][0]
prefDate = datetime.datetime.strptime(prefDate, '%Y-%m-%d').date()
for date, quantity in all_results:
    curDate = datetime.datetime.strptime(date, '%Y-%m-%d').date()
    while prefDate!=curDate:
        arr.append(0)
        prefDate=prefDate + datetime.timedelta(days=1)
    arr.append(quantity)
    prefDate=prefDate + datetime.timedelta(days=1)


model = ARIMA(arr, order=(1,0,3))
results = model.fit()
pred = results.get_prediction(start=len(arr)+1, end=len(arr)+5, dynamic=False)
pred_ci = pred.conf_int(alpha=0.05)


for a, b in pred_ci:
    data =(id, prefDate, int((a+b)/2))
    cur.execute("INSERT INTO prediction VALUES(?, ?, ?);", data)
    prefDate= prefDate + datetime.timedelta(days=1)
conn.commit()
conn.close()
