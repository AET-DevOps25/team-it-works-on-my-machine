output "public_ip" {
  value = aws_instance.vm.public_ip
  description = "Public IP of the EC2 instance"
}