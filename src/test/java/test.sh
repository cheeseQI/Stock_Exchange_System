#!/bin/bash

ARG1=${1:-1}
ARG2=${2:-0}

for (( a=0; a<1;a++ ))
do
  for (( c=$ARG2; c<$ARG1;c++ ))
  do
     nc localhost 12345 < "../resources/test$c.txt" > "./data/out$c.txt" &
  done
done

echo "nc localhost 12345 ../resources/test[$ARG2-$ARG1].txt done"
wait
echo "finish all sending command"
