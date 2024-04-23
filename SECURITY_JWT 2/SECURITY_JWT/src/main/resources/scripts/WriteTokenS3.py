import boto3
from botocore.exceptions import ClientError
import logging
import sys
 
# Configure logging
logging.basicConfig(level=logging.DEBUG)
 
BUCKET_NAME = 'sud-test-token-test'
FILE_NAME = 'tokens.csv'
 
s3_client = boto3.client('s3')
 
def create_bucket_if_not_exists(bucket_name):
    try:
        s3_client.head_bucket(Bucket=bucket_name)
    except ClientError as e:
        if e.response['Error']['Code'] == '404':
            s3_client.create_bucket(Bucket=bucket_name, CreateBucketConfiguration={'LocationConstraint': 'ap-south-1'})
 
def append_or_update_token_to_s3(bucket_name, file_name, user_id, token):
    create_bucket_if_not_exists(bucket_name)
    try:
        # Check if the file exists
        s3_client.head_object(Bucket=bucket_name, Key=file_name)
    except ClientError as e:
        if e.response['Error']['Code'] == '404':
            # If the file doesn't exist, create a new one
            s3_client.put_object(Body="UserId,Token\n", Bucket=bucket_name, Key=file_name, ContentType='text/csv')
    try:
        # Read the existing content
        obj = s3_client.get_object(Bucket=bucket_name, Key=file_name)
        existing_data = obj['Body'].read().decode('utf-8')
        lines = existing_data.strip().split('\n')
        # Update or append the new token
        updated_lines = []
        user_exists = False
        for line in lines:
            if line.startswith(user_id):
                updated_lines.append(f"{user_id},{token}")
                user_exists = True
            else:
                updated_lines.append(line)
        if not user_exists:
            updated_lines.append(f"{user_id},{token}")
        updated_data = '\n'.join(updated_lines)
        # Write the updated content back to S3
        s3_client.put_object(Body=updated_data, Bucket=bucket_name, Key=file_name, ContentType='text/csv')
        logging.debug(f"Successfully updated/added token {token} for user {user_id} in file {file_name}.")
    except ClientError as e:
        logging.error(f"Error appending or updating token to S3: {e}")
 
if __name__ == "__main__":
    logging.debug("Starting Python script...")
    if len(sys.argv) != 3:
        logging.error("Usage: python WriteTokenS3.py <user_id> <token>")
        sys.exit(1)
 
    user_id = sys.argv[1]
    token = sys.argv[2]
 
    logging.debug(f"Received user ID: {user_id}")
    logging.debug(f"Received token: {token}")
 
    append_or_update_token_to_s3(BUCKET_NAME, FILE_NAME, user_id, token)
    logging.debug("Python script completed successfully.")
