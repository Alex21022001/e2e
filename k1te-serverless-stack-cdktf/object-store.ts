import { S3Bucket } from "@cdktf/provider-aws/lib/s3-bucket";
import { S3BucketPolicy } from "@cdktf/provider-aws/lib/s3-bucket-policy";
import { S3BucketPublicAccessBlock } from "@cdktf/provider-aws/lib/s3-bucket-public-access-block";
import { TerraformOutput } from "cdktf";
import { Construct } from "constructs";
import { S3 } from "iam-floyd/lib/generated";
import { Grantable } from "./grantable";

export type ObjectStoreProps = {
  bucketPrefix: string;
  allowAnonymousRead?: boolean;
};

export class ObjectStore extends Construct {
  readonly bucket: S3Bucket;
  constructor(
    scope: Construct,
    id: string,
    { bucketPrefix, allowAnonymousRead = false }: ObjectStoreProps
  ) {
    super(scope, id);

    this.bucket = new S3Bucket(this, "object-store", {
      bucketPrefix,

      lifecycleRule: [
        {
          id: "expire-objects",
          expiration: { days: 7 },
          enabled: true,
        },
      ],
      corsRule: [
        {
          allowedMethods: ["GET", "PUT", "HEAD"],
          allowedOrigins: ["*"],
          allowedHeaders: ["*"],
          exposeHeaders: [],
        },
      ],
    });

    if (allowAnonymousRead) {
      new S3BucketPublicAccessBlock(this, "object-store-public-access-block", {
        bucket: this.bucket.id,
        blockPublicAcls: true,
        blockPublicPolicy: false,
        ignorePublicAcls: true,
        restrictPublicBuckets: false,
      });

      new S3BucketPolicy(this, "allow-anonymous-read", {
        bucket: this.bucket.id,
        policy: JSON.stringify({
          Version: "2012-10-17",
          Statement: [
            new S3()
              .allow()
              .forPublic()
              .toGetObject()
              .onObject(this.bucket.bucket, "*")
              .toJSON(),
          ],
        }),
      });
    }

    new TerraformOutput(this, "bucket-name", {
      value: this.bucket.bucket,
    });
  }

  public allowReadWrite(to: Grantable) {
    const policyStatement = new S3()
      .allow()
      .toGetObject()
      .toPutObject()
      .toDeleteObject()
      .toListBucket()
      .on(this.bucket.arn);
    to.grant(`allow-crud-${this.bucket.bucket}`, policyStatement);
    return this;
  }
}
