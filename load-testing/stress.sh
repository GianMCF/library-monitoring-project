#!/bin/bash

URL=$1

if [ -z "$URL" ]; then
  echo "Uso:"
  echo "./stress.sh <url>"
  exit 1
fi

REQUESTS=${2:-500}
CONCURRENCY=${3:-10}

echo ""
echo "URL: $URL"
echo "Requests: $REQUESTS"
echo "Concurrency: $CONCURRENCY"
echo ""

for ((i=1;i<=REQUESTS;i++))
do
(
  curl -s "$URL" > /dev/null

  if (( i % 50 == 0 ))
  then
    echo "$i requests enviados"
  fi
) &

if (( $(jobs -r | wc -l) >= CONCURRENCY ))
then
  wait -n
fi

done

wait

echo ""
echo "Carga finalizada."