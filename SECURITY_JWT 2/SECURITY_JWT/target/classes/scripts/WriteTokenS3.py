import boto3
from botocore.exceptions import ClientError
import logging
import sys

# Configure logging
logging.basicConfig(level=logging.DEBUG)

BUCKET_NAME = 'sud-test-token-test'
FILE_NAME = 'tokens.csv'

# Function to create bucket if it doesn't exist
def create_bucket_if_not_exists(bucket_name):
    logging.debug("Creating bucket if not exists...")
    s3_client = boto3.client('s3', region_name='ap-south-1')
    
    try:
        s3_client.head_bucket(Bucket=bucket_name)
        logging.debug(f"Bucket {bucket_name} already exists.")
    except ClientError as e:
        if e.response['Error']['Code'] == '404':
            s3_client.create_bucket(Bucket=bucket_name, CreateBucketConfiguration={'LocationConstraint': 'ap-south-1'})
            logging.debug(f"Created new bucket: {bucket_name}")
        else:
            logging.error(f"Error checking bucket existence: {e}")

# Function to create CSV file if it doesn't exist
def create_csv_file_if_not_exists(bucket_name, file_name):
    logging.debug("Creating CSV file if not exists...")
    s3_client = boto3.client('s3')
    
    try:
        s3_client.head_object(Bucket=bucket_name, Key=file_name)
        logging.debug(f"File {file_name} already exists in bucket {bucket_name}.")
    except ClientError as e:
        if e.response['Error']['Code'] == '404':
            s3_client.put_object(Body="UserId,Token\n", Bucket=bucket_name, Key=file_name)
            logging.debug(f"Created new file {file_name} in bucket {bucket_name}.")
        else:
            logging.error(f"Error checking file existence: {e}")

# Function to append or update a token to the CSV file in S3
def append_or_update_token_to_s3(bucket_name, file_name, user_id, token):
    logging.debug("Appending or updating token to S3...")
    s3_client = boto3.client('s3')
    csv_data = f"{user_id},{token}\n"

    try:
        create_bucket_if_not_exists(bucket_name)
        create_csv_file_if_not_exists(bucket_name, file_name)

        existing_data = s3_client.get_object(Bucket=bucket_name, Key=file_name)['Body'].read().decode('utf-8')
        
        lines = existing_data.split('\n')
        user_exists = False
        updated_lines = []

        for line in lines:
            if user_id in line:
                updated_lines.append(f"{user_id},{token}")
                user_exists = True
            elif line:
                updated_lines.append(line)

        if not user_exists:
            updated_lines.append(csv_data)
        
        updated_data = '\n'.join(updated_lines)
        
        s3_client.put_object(Body=updated_data, Bucket=bucket_name, Key=file_name, ContentType='text/csv')
        
        if user_exists:
            logging.debug(f"Updated token {token} for user {user_id} in file {file_name}.")
        else:
            logging.debug(f"Appended token {token} for new user {user_id} to file {file_name}.")

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
