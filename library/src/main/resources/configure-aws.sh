source vars.sh
rm -f config
touch config
echo "[default]" >> config
echo "region = "$AWS_REGION >> config
cp config ~/.aws/

rm -f credentials
touch credentials
echo "[default]" >> credentials
echo "aws_access_key_id = "$AWS_ACCESS_KEY_ID >> credentials
echo "aws_secret_access_key = "$AWS_SECRET_ACCESS_KEY >> credentials
cp credentials ~/.aws/
