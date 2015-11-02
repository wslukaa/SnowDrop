import numpy as np #numpy package
import pickle as pkl #pickle package
import matplotlib.pyplot as plt
import time
import sqlite3
import string
import math

def normalize(X):
    'normalize X into 0 mean 1 variance data for each column'
    ns, _ = X.shape
    mean = np.sum(X, axis=0)/ns
    X -= mean[None, :]
    Var = np.sum(X**2, axis=0)/ns
    X = X / np.sqrt(Var)[None, :]
    return X, mean, Var


def softmax(x):
    if len(x.shape) == 1:
        xmax = np.amax(x)
        w = np.exp(x - xmax)
        w = w / np.sum(w)
    else:
        xmax = np.amax(x, axis=1)
        xmax = xmax[:, None]
        w = np.exp(x - xmax)
        w = w / (np.sum(w, axis=1))[:, None]
    return w


def geneMoeData():
    # generate regression data
    x = np.random.rand(1000, 1)
    r = np.zeros((1000, 1))
    r[x < 0.5] = x[x < 0.5] * 2 + np.random.randn(1000, 1)[x < 0.5] * 0.1
    r[x >= 0.5] = (2 - x[x >= 0.5] * 2) + np.random.randn(1000, 1)[x >= 0.5] * 0.1

    tx = x[0:700, :]
    tr = r[0:700, :]
    vx = x[700:, :]
    vr = r[700:, :]
    return tx, tr, vx, vr


###############################################################
# Mixture of experts model
# gating network: g1, g2,..., gk with 0 < gi < 1, sum gi = 1
#                 softmax model
#                 gi \propto exp(etai * x)
# expert network: linear model
#                 mui = thetai * x
# model likelihood:
#            P = sum_i P(i|x,eta)P(y|x,i,theta)
###############################################################


class MoE(object):
    """docstring for moe"""
    def __init__(self, k=2, lr=0.1, decay=0.99, maxIter=200, rho=1e-2):
        self.k = k
        self.lr = lr
        self.decay = decay
        self.maxIter = maxIter
        self.rho = rho

    def fit(self, X, y):
        # X, y are both matrices
        # X (ns * nf)
        # y (ns * 1)
        ns, nf = X.shape
        # add constant column
        X = np.hstack((np.ones((ns, 1)), X))
        nf += 1
        # initialize parameters, random init to break symmetry! important!
        u = np.random.rand(nf, self.k) * 0.02 - 0.01
        v = np.random.rand(nf, self.k) * 0.02 - 0.01
        # u = np.zeros((nf, self.k))
        # v = np.zeros((nf, self.k))
        k = self.k
        rate = self.lr
        decay = self.decay
        maxIter = self.maxIter
        rho = self.rho
        iternum = 0
        err = np.infty

        while (err > 1e-5) & (iternum < maxIter):
            perms = np.random.permutation(ns)
            for j in range(ns):
                t = perms[j]
                mu = X[t, :][None, :].dot(u)
                g = softmax(X[t, :][None, :].dot(v))
                # h = g * np.exp(- 0.5 * (y[t] - mu) ** 2)
                # multivariate Gaussian prior on u
                logl = - 0.5 * (y[t] - mu) ** 2 - 0.5 * np.sum(u**2, axis=0) / nf / k * rho
                l = np.exp(logl - np.max(logl))
                h = g * l
                h = h / np.sum(h)
                for i in range(k):
                    # u[:, i][:, None] += rate * h[0, i] * ((y[t] - mu[0, i]) * X[t, :][:, None])
                    u[:, i][:, None] += rate * h[0, i] * ((y[t] - mu[0, i]) * X[t, :][:, None] - 2 * u[:, i][:, None] / nf / k * rho)
                    v[:, i][:, None] += rate * (h[0, i] - g[0, i]) * X[t, :][:, None]
            mu = X.dot(u)
            g = softmax(X.dot(v))
            yhat = np.sum(mu * g, axis=1)
            rate = rate * decay
            iternum += 1
            err = np.linalg.norm(yhat - y[:, 0])/ns
            print "Error:", err

        self.u = u
        self.v = v

    def predict(self, X):
        u = self.u
        v = self.v
        ns, _ = X.shape
        X = np.hstack((np.ones((ns, 1)), X))
        mu = X.dot(u)
        g = softmax(X.dot(v))
        yhat = np.sum(mu * g, axis=1)
        return yhat


def test():
    tx, tr, vx, vr = geneMoeData()
    tr = tr
    vr = vr
    moe = MoE(k=2, lr=.5, decay=0.99, maxIter=200, rho=1e-2) #
    moe.fit(tx, tr) #data
    yhat = moe.predict(vx)  # yhat is row vector
    plt.figure()
    #    plt.plot(vx, vr, 'ob')
    #   plt.plot(vx, yhat, '*r')
    #   plt.show()


class SnowDrop(object):
    
    database = []
    cnt=0

    model_list = []

    moe_pm = MoE(k=2, lr=.5, decay=0.99, maxIter=200, rho=1e-2)
    
    def __init__(self):
        print "this is the input interface"
        print "you need to add your input into this class"

    def train_model(self,id):
        print self.database[0][7]
        train_cnt = 0
        for i in range(self.cnt-1):
            flg = 0
            for j in (2,3,4,5,7,8,9):
                #print "type",math.isnan(self.database[i][j]),self.database[i][j],type(self.database[i][j])
                #print self.database[i][j],
                if (type(self.database[i][j]) != float and type(self.database[i][j])!=int):
                    flg=1
                    break
                elif not self.database[i][j]:
                    flg=1
                    break
            #print ""
            
            if (flg == 0) and (not(not self.database[i+1][id])):
                #print self.database[i+1][id],(flg == 0) and ((type(self.database[i+1][id]) == float) or (type(self.database[i+1][id]== int)))
                #print type(self.database[i+1][id]),self.database[i+1][id]
                if not math.isnan(self.database[i+1][id]):
                    train_cnt+=1
            #    print "YES"
            #print train_cnt
    
    
    
        #print type(self.database[0][9])
        #print self.database[0][9]
        print "cnt first: ", train_cnt
        print "id:",id
        X = np.zeros((train_cnt,7))
        Y = np.zeros((train_cnt,1))
        train_cnt = 0
        for i in range(self.cnt-1):
            flg = 0
            for j in (2,3,4,5,7,8,9):
                #print "type",math.isnan(self.database[i][j])
                if (type(self.database[i][j]) != float and type(self.database[i][j])!=int):
                    flg=1
                    #print i,j
                    #    print self.database[i][1]
                    break
                elif not self.database[i][j]:
                    flg=1
                    break
    
            #print flg
            if flg == 0 and (type(self.database[i+1][id]) == float or type(self.database[i+1][id]== int)):
                if not (not self.database[i+1][id]):
                    z = 0
                    for j in (2,3,4,5,7,8,9):
                    #print j,
                        #print self.database[i][j],
                        X[train_cnt,z] =  self.database[i][j]
                        z+=1
                    Y[train_cnt,0]=self.database[i+1][id]
                    train_cnt+=1
            #print ""
        print "cnt:",train_cnt

        normalize(X)

        for i in range(train_cnt):
            for j in range(7):
                print X[i,j],
            print Y[i,0]

        moe = MoE(k=2, lr=.5, decay=0.99, maxIter=200, rho=1e-2)
        moe.fit(X,Y)
        return moe
                
            



    def test_by_model(self):
        answer = 0
        for i in range(self.cnt):
            flg = 0
            for j in (2,3,4,5,7,8,9):
                #print "type",math.isnan(self.database[i][j]),self.database[i][j],type(self.database[i][j])
                #print self.database[i][j],
                if (type(self.database[i][j]) != float and type(self.database[i][j])!=int):
                    flg=1
                    break
                elif not self.database[i][j]:
                    flg=1
                    break
        
            if flg ==0:
                answer = i
        print "success"
        print self.database[answer][0]
        X = np.zeros((1,7))
        z = 0
        for i in (2,3,4,5,7,8,9):
            X[0,z]=self.database[answer][i]
            print X[0,z],
            ++z
        print ""
        
        normalize(X)
        
        for i in range(7):
            print X[0,i],
        print ""
        
        
        Y=self.model_list[4].predict(X)
        
        return Y[0]

    def get_train(self):
        self.model_list = []
        print "this interface makes you train the model"
        for i in (2,3,4,5,7,8,9):
            self.model_list.append(self.train_model(i))
        #train(X)

    def get_test(self):
        print "this interface makes you test the pm"
        print "and return the pm score"
        conn = sqlite3.connect("update.db")
        #conn.execute("delete from student")
        # two ways :
        # c = conn.execute("insert into student values ('tom', 12)")
        c = conn.execute("insert into airweather (timestamp, pm) values ('2015-10-22-9', 250)")
        conn.commit()
        conn.close()

        
        
        result = self.test_by_model()
        print "test result: ",result
        # Y=test()

    def update_database(self):
        self.database = []
        self.cnt=0;
        conn = sqlite3.connect('update.db')
        c = conn.execute("SELECT num, timestamp, temperature, humidity, pressure, speed, condition, pm, dew, rain, direction  FROM airweather")
        for row in c:
            flag = 0
            self.cnt+=1
            self.database.append(row)
        print "update the database"
        print self.cnt
        conn.close()

    def run(self):
        j = 166
        while(1):
            self.update_database()
            j+=1
            if(j==167):
                self.get_train()
                j=0;

            self.get_test()
            print  "time:",
            print time.localtime()
            time.sleep(3600)

test = SnowDrop()
test.run()
#test.get_train()
#test.get_test()
#print time.localtime().tm_year

