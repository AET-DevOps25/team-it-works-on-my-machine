provider "aws" {
  region  = "us-east-1"
  profile = "default"
}

resource "aws_key_pair" "deployer" {
  key_name   = "terraform-key"
  public_key = file(var.public_key_path)
}

data "aws_vpc" "default" {
  default = true
}

resource "aws_security_group" "ssh_access" {
  name        = "allow_ssh"
  description = "Allow SSH inbound traffic"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "vm" {
  ami                    = "ami-084568db4383264d4"
  instance_type          = "t2.micro"
  key_name               = aws_key_pair.deployer.key_name
  associate_public_ip_address = true
  vpc_security_group_ids = [aws_security_group.ssh_access.id]

  tags = {
    Name = "team-it-works-on-my-machine"
  }

provisioner "local-exec" {
  command = "echo [docker_server] > ..\\ansible\\inventory.ini && echo ${self.public_ip} ansible_user=ubuntu ansible_ssh_private_key_file=~/.ssh/id_rsa >> ..\\ansible\\inventory.ini"
}
}
