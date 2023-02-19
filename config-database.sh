export WALLET=wallet-service.wallets

echo "####### Trying to drop the table if it already exists #######"
awslocal dynamodb delete-table --table-name $WALLET

echo "####### Creating wallet-service.wallers table #######"
awslocal dynamodb create-table \
  --table-name $WALLET \
  --attribute-definitions \
  AttributeName=user_id,AttributeType=S \
  --key-schema \
  AttributeName=user_id,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \

echo "####### Creating wallet-service.wallers table #######"
awslocal dynamodb put-item --table-name $WALLET --item "{\"user_id\":{\"S\":\"233e79ea-2fd1-4904-a14a-6bb8b17e1fe4\"},\"amount\":{\"N\":\"1000\"}}"
awslocal dynamodb put-item --table-name $WALLET --item "{\"user_id\":{\"S\":\"308ef9ae-c05f-4a87-a0f3-658230e2977c\"},\"amount\":{\"N\":\"500\"}}"