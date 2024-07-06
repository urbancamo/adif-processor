# DEVOPS Notes

## Publishing to Maven Central

Publishing to Maven Central is done via GitHub Actions. The access username and password are stored in the `~/.m2/settings.xml` file, and also in the GitHub secrets: https://github.com/urbancamo/adif-processor/settings/secrets/actions `OSSRH_USERNAME` and `OSSRH_PASSWORD`.

Location of adif-processor on maven central is: https://central.sonatype.com/artifact/uk.m0nom/adif-processor

## Upgrading Java Version on Elastic Beanstalk

To configure the new environment:

 - Configure service access: set existing service role: `aws-elasticbeanstalk-service-role`, EC2 key pair: `aws-eb` and EC2 instance profile: `aws-elasticbeanstalk-ec2-role`

Additional environment variables must be set:
 - `QRZ_USERNAME`
 - `QRZ_PASSWORD`
 - `AWS_ACCESS_KEY`
 - `AWS_SECRET_KEY`

## Load Balancer
    
If you deploy a new application environment to Elastic Beanstalk, you will need to add a load balancer to the environment. This is done by going to the Elastic Beanstalk console, selecting the environment, and then clicking on the "Load Balancer" tab. You will need to create a new load balancer.

When you define the listener, ensure that you assign the adif.uk certificate to the listener.

NOTE: when registering a target group with the load balancer, you must ensure you set the port number is 80 througout.

You may need to create a new security group for the load balancer that allows traffic from the internet, with default rule IP access from 0.0.0.0/0.

The security group should have a Custom TCP rule for inbound traffic on port 443.

## DNS Configuration

Update DNS management via the cPanel Zone editor on CloudAbove to point to the load balancer DNS name A record.

Ensure the certificate CNAME records defined in the AWS certificate are setup correctly as CNAME records against the domain adif.uk and www.adif.uk in CloudAbove.

## RDS Postgres Configuration

Need to modify the RDS Postgres configuration to add a security group to allow access via the internet.

Ensure the security group associated with the RDS Postgres instance allows access to the internet.

Add the following environment variables to the Elastic Beanstalk environment:

- `POSTGRES_USERNAME`
- `POSTGRES_PASSWORD`
- `POSTGRES_DB`
- `POSTGRES_HOST`
- `SPRING_PROFILES_ACTIVE` = `prod`

