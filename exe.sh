#! /bin/bash

var1=`cat Makefile | sed '/^#/d' | cut -f 3 -d " " | sed '/^$/d' | cut -f 1 -d .`

if [$1 == ""]
then
	./arch-x86_64-Linux/$var1 -c ../configs/square.txt
else
	./arch-x86_64-Linux/$var1 -c ../configs/$1
fi
