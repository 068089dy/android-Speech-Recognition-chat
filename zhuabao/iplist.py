#! /usr/bin/python2
# coding:utf-8

from scapy.all import *
from time import ctime,sleep
import threading
TIMEOUT = 4
conf.verb=0


def pro(cc):
    dst = "172.18.208." + str(cc)
    packet = IP(dst=dst, ttl=20)/ICMP()
    reply = sr1(packet, timeout=TIMEOUT)
    if not (reply is None):
        #handle.write(reply.src+" is online"+"\n")
        print reply.src, "is online"

def main():
    threads=[]
    f=open('ip.log','w')
    for i in range(2,254):
        t=threading.Thread(target=pro,args=(i,))
        threads.append(t)
    print "main Thread begins at ",ctime()
    for t in threads :
        try:
            t.start()
        except:
            continue
    for t in threads :
        try:
            t.join()
        except:
            continue
    print "main Thread ends at ",ctime()

if __name__=="__main__" :
    main();
