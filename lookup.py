import sqlite3
import numpy as np #numpy package
import pickle as pkl #pickle package
import matplotlib.pyplot as plt
import time

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


def ttts():
    x = np.zeros((1000,3))
    y = np.zeros((1000,1))
    #z = np.zeros((1000,3))
    for i in range(1000):
        x[i,0]=x[i,1]=x[i,2]=i/1000
        #    z[i,0]=z[i,1]=z[i,2]=i/1000
        y[i,0]=i;

#    print x[200,2], y[200,0]

    normalize(x)
    #    normalize(y)

#    normalize(z)
#    print x[200,2],y[200,0]

    moe = MoE(k=2, lr=.5, decay=0.99, maxIter=200, rho=1e-2) #
    moe.fit(x, y) #data

    yhat = moe.predict(x)
    
    for i in range(20):
        print yhat[i], y[i,0]

#    print nx,ny
#    return 0

def main():

#    ttts()

    conn = sqlite3.connect('update.db')
#	c = conn.execute("SELECT timestamp, pm FROM airweather")
#	for row in c:
#		if row[0] == inputTime:
#			print "timestamp = ", row[0], "pm = ", row[1]
#    print "Operation done Successfully"
    c = conn.execute("SELECT num, timestamp, temperature, humidity, pressure, speed, condition, pm, dew, rain, direction  FROM airweather")

# row[1]->temperature row[6]->pm

    database = []
    trainList = []
    pmTestList = []
    temperatureTestList = []
    cnt = 0
    
    
    
    for row in c:
        flag = 0
        for j in range(11):
            print row[j],
        print ""
        cnt+=1
        database.append(row)

#    print database[0][0]
#    print cnt


#    conn.execute("HELP")
    conn.close()

if __name__ == '__main__':
	main()
