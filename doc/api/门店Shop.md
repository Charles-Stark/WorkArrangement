# API - 门店增删改查相关

## 添加门店
管理员添加门店时，发送请求如下。

POST请求 /api/shop/add
```yaml
name: string 门店名称
address: string 门店地址
size: double 门店面积
manager: long 门店管理员id
```
```json
{
  "code": 0,
  "message": "添加门店成功",
  "data": {
    "id": 1,
    "name": "apple",
    "address": "san jose",
    "size": 12.34,
    "manager": 123
  }
}
```
```json
{
  "code": -1,
  "message": "添加门店失败",
  "data": null
}
```

## 删除门店
管理员删除门店时，发送请求如下。

POST请求 /api/shop/delete
```yaml
id: long 门店id
```
```json
{
  "code": 0,
  "message": "删除门店成功",
  "data": null
}
```
```json
{
  "code": -1,
  "message": "删除门店失败",
  "data": null
}
```

## 获取门店信息
获取门店信息时，发送请求如下。

GET请求 /api/shop/get/{id}
```yaml
id: long 门店id
```
```json
{
  "code": -1,
  "message": "获取门店信息成功",
  "data": {
    "id": 1,
    "name": "apple",
    "address": "san jose",
    "size": 12.34,
    "manager": 123
  }
}
```
```json
{
  "code": -1,
  "message": "获取门店信息失败",
  "data": null
}
```

## 修改门店信息
修改门店信息时，发送请求如下。四个可选参数至少有其一。

POST请求 /api/shop/update
```yaml
id: long 门店id（必填）
name: string 门店名称（可选）
address: string 门店地址（可选）
size: double 门店面积（可选）
manager: long 门店管理员id（可选）
```
```json
{
  "code": 0,
  "message": "更新门店成功",
  "data": {
    "id": 1,
    "name": "banana",
    "address": "san jose",
    "size": 12.34,
    "manager": 123
  }
}
```
```json
{
  "code": -1,
  "message": "更新门店失败",
  "data": null
}
```
