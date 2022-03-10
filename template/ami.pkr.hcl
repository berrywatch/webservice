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
  provisioner "file" {
  source = "${var.webservice}"
  destination = "/tmp/webservice"
  }
  sources = [
    "source.amazon-ebs.example"
  ]

  provisioner "shell" {
    inline = [
      "echo Connected via SSM at '${build.User}@${build.Host}:${build.Port}'",
      "sudo yum update -y",
      "sudo yum install java -y",
      "wget https://dlcdn.apache.org/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.tar.gz",
      "tar xvf apache-maven-3.8.4-bin.tar.gz",
      "sudo mv apache-maven-3.8.4  /usr/local/apache-maven",
      "rm apache-maven-3.8.4-bin.tar.gz",
      "export M2_HOME=/usr/local/apache-maven && export M2=$M2_HOME/bin && export PATH=$M2:$PATH",
      "mkdir project",
      "cd project",
      "mv /tmp/webservice ."
      ]
  }
}