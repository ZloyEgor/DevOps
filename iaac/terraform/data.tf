data "yandex_vpc_subnet" "a" {
  name      = "default-ru-central1-a"
  folder_id = local.folder_id
}