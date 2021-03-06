variable "aws_access_key" {
  type    = string
  default = ""
}

variable "aws_region" {
  type    = string
  default = ""
}

variable "aws_secret_key" {
  type    = string
  default = ""
}

variable "source_ami" {
  type    = string
  default = "ami-033b95fb8079dc481"
}

variable "ssh_username" {
  type    = string
  default = "ec2-user"
}

variable "subnet_id" {
  type    = string
  default = ""
}

variable "webservice" {
  type = string
  default = ""
}

variable "ami_users" {
  type = list(string)
  default = ["270484512737","269494922587"]
}

locals { timestamp = regex_replace(timestamp(), "[- TZ:]", "") }

packer {
  required_plugins {
    amazon = {
      version = ">= 0.0.2"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

source "amazon-ebs" "example" {
  access_key      = "${var.aws_access_key}"
  ami_description = "Ubuntu AMI for CSYE 6225"
  ami_name        = "csye6225_spring2022_${local.timestamp}"
  instance_type   = "t2.micro"
  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/xvda"
    volume_size           = 8
    volume_type           = "gp2"
  }
  region       = "${var.aws_region}"
  secret_key   = "${var.aws_secret_key}"
  source_ami   = "${var.source_ami}"
  ssh_username = "${var.ssh_username}"
  subnet_id    = "${var.subnet_id}"
  ami_users    = "${var.ami_users}"
}

build {
  name = "learn-packer"
  
  sources = [
    "source.amazon-ebs.example"
  ]

  provisioner "shell" {
    inline = [
      "echo Connected via SSM at '${build.User}@${build.Host}:${build.Port}'",
      "sudo yum update -y",
      "sudo yum install java -y",
      "sudo yum install ruby -y",
      "sudo yum install amazon-cloudwatch-agent -y",
      "wget https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz",
      "tar xvf apache-maven-3.8.5-bin.tar.gz",
      "sudo mv apache-maven-3.8.5  /usr/local/apache-maven",
      "rm apache-maven-3.8.5-bin.tar.gz",
      "cd /home/ec2-user/",
      "wget https://aws-codedeploy-us-east-1.s3.us-east-1.amazonaws.com/latest/install",
      "chmod +x ./install",
      "sudo ./install auto",
      "sudo mkdir /var/webapp"
      ]
  }
}