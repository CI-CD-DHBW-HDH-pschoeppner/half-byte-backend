variable "docker_droplet_image" {
  default = "docker-20-04"
}


resource "digitalocean_ssh_key" "default" {
  name       = "ticTacToe-half-byte"
  public_key = file("C:/workspace/ssh_key.pub")
}

resource "digitalocean_droplet" "half-byte-backend" {
  image = var.docker_droplet_image # Variablen werden mit var.<name> referenziert
  size   = "s-1vcpu-1gb" # Die kleinst möglichste für das Image
  region = "fra1" # Frankfurt 1
  ssh_keys = [digitalocean_ssh_key.default.fingerprint]
  name     = "half-byte-backend"
}



terraform {
  required_providers {
    digitalocean = {
      source = "digitalocean/digitalocean"
      version = "~> 2.0"
    }
  }
}

variable "do_token" {}

# Configure the DigitalOcean Provider
provider "digitalocean" {
  token = var.do_token
}
