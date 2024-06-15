# DEVOPS Notes

## Publishing to Maven Central

Publishing to Maven Central is done via GitHub Actions. The access username and password are stored in the `~/.m2/settings.xml` file, and also in the GitHub secrets: https://github.com/urbancamo/adif-processor/settings/secrets/actions `OSSRH_USERNAME` and `OSSRH_PASSWORD`.

Location of adif-processor on maven central is: https://central.sonatype.com/artifact/uk.m0nom/adif-processor

## Upgrading Java Version on Elastic Beanstalk

To configure the new environment:

 - Configure service access: set existing service role: `aws-elasticbeanstalk-service-role`, EC2 key pair: `aws-eb` and EC2 instance profile: `aws-elasticbeanstalk-ec2-role`

Additional environment variables most be set:
 - QRZ_USERNAME
 - QRZ_PASSWORD