# API - 员工录入/账号生成相关

## 员工账号生成
管理员用户在导入员工时，每导入一位员工，将会在```用户User数据表```、```偏好Preference数据表```和```员工Employee数据表```分别生成一条记录，三张表中的记录通过```id```字段关联，以示为同一人。

员工的初始密码为```邮箱'@'符前的内容```+```123456```. 例如，邮箱为```joe@example.com```, 则初始密码为```joe123456```.

导入时，发送请求如下

POST请求 /api/employee/add
```yaml
email: String 邮箱
username: String 用户名
position: String 员工职位，可选值：门店经理，副经理，小组长，收银，导购，库房
shop: Long 店铺id
salary: Double 员工薪资
workingDay: String （可选）工作日偏好，数字表示星期几，英文逗号分隔，例：1,3,4
workingHours: String （可选）工作时间偏好，例：08:00-12:00,18:00-22:00
durationOfShift: Integer （可选）班次时长偏好，每天时长不超过多少小时，例：4
durationOfWeek: Integer （可选）每周最多工作时间，每周时长不超过多少小时，例：20
```
```json
{
  "code": 0,
  "message": "添加员工成功",
  "data": {
    "id": 3,
    "uid": "AZ0001",
    "email": "joe@example.com",
    "username": "joe",
    "position": "门店经理",
    "shop": 1023,
    "salary": 5000.0,
    "time": 0,
    "workingDay": "1,3,4",
    "workingHours": "08:00-12:00,18:00-22:00",
    "durationOfShift": 4,
    "durationOfWeek": 20
  }
}
```
```json
{
  "code": -1,
  "message": "添加员工失败",
  "data": null
}
```

## 删除员工
删除时，发送请求如下

POST请求 /api/employee/delete
```yaml
id: Long 员工id
```
```json
{
  "code": 0,
  "message": "删除员工成功",
  "data": null
}
```
```json
{
  "code": 0,
  "message": "删除员工失败",
  "data": null
}
```

## 获取单个员工信息
获取单个员工信息。

获取时，发送请求如下

GET请求 /api/employee/get/{id}
```yaml
id: Long 员工id
```
```json
{
  "code": 0,
  "message": "获取员工成功",
  "data": {
    "id": 3,
    "uid": "AZ0001",
    "email": "joe@example.com",
    "username": "joe",
    "position": "门店经理",
    "shop": 1023,
    "salary": 5000.0,
    "time": 0,
    "workingDay": "1,3,4",
    "workingHours": "08:00-12:00,18:00-22:00",
    "durationOfShift": 4,
    "durationOfWeek": 20
  }
}
```
```json
{
  "code": 0,
  "message": "获取员工失败",
  "data": null
}
```

## 获取某店铺所有员工信息
获取某店铺所有员工信息。

获取时，发送请求如下

GET请求 /api/employee/get/byShop/{shopId}
```yaml
shopId: Long 店铺id
```
```json
{
  "code": 0,
  "message": "获取员工成功",
  "data": [
    {
      "id": 1,
      "uid": "AZ0001",
      "email": "joe@example.com",
      "username": "joe",
      "position": "门店经理",
      "shop": 1023,
      "salary": 5000.0,
      "time": 0,
      "workingDay": "1,3,4",
      "workingHours": "08:00-12:00,18:00-22:00",
      "durationOfShift": 4,
      "durationOfWeek": 20
    },
    {
      "id": 2,
      "uid": "AZ0002",
      "email": "sam@example.com",
      "username": "sam",
      "position": "副经理",
      "shop": 1023,
      "salary": 4000.0,
      "time": 0,
      "workingDay": "1,3,4",
      "workingHours": "08:00-12:00,18:00-22:00",
      "durationOfShift": 4,
      "durationOfWeek": 20
    },
    {
      "id": 3,
      "uid": "AZ0003",
      "email": "josh@example.com",
      "username": "josh",
      "position": "收银",
      "shop": 1023,
      "salary": 4000.0,
      "time": 0,
      "workingDay": "1,3,4",
      "workingHours": "08:00-12:00,18:00-22:00",
      "durationOfShift": 4,
      "durationOfWeek": 20
    }
  ]
}
```
```json
{
  "code": 0,
  "message": "获取员工失败",
  "data": null
}
```

## 获取某管理员下属所有员工信息
获取某管理员下属所有员工信息，按店铺id分类。

获取时，发送请求如下

GET请求 /api/employee/get/byManager/{managerId}
```yaml
managerId: Long 管理员id
```
```json
{
  "code": 0,
  "message": "获取员工成功",
  "data": {
    "1023": [
      {
        "id": 1,
        "uid": "AZ0001",
        "email": "joe@example.com",
        "username": "joe",
        "position": "门店经理",
        "shop": 1023,
        "salary": 5000.0,
        "time": 0,
        "workingDay": "1,3,4",
        "workingHours": "08:00-12:00,18:00-22:00",
        "durationOfShift": 4,
        "durationOfWeek": 20
      },
      {
        "id": 2,
        "uid": "AZ0002",
        "email": "sam@example.com",
        "username": "sam",
        "position": "副经理",
        "shop": 1023,
        "salary": 4000.0,
        "time": 0,
        "workingDay": "1,3,4",
        "workingHours": "08:00-12:00,18:00-22:00",
        "durationOfShift": 4,
        "durationOfWeek": 20
      },
      {
        "id": 3,
        "uid": "AZ0003",
        "email": "josh@example.com",
        "username": "josh",
        "position": "收银",
        "shop": 1023,
        "salary": 4000.0,
        "time": 0,
        "workingDay": "1,3,4",
        "workingHours": "08:00-12:00,18:00-22:00",
        "durationOfShift": 4,
        "durationOfWeek": 20
      }
    ],
    "1024": [
      {
        "id": 5,
        "uid": "AP0001",
        "email": "matt@example.com",
        "username": "matt",
        "position": "门店经理",
        "shop": 1024,
        "salary": 5000.0,
        "time": 0,
        "workingDay": "1,3,4",
        "workingHours": "08:00-12:00,18:00-22:00",
        "durationOfShift": 4,
        "durationOfWeek": 20
      },
      {
        "id": 6,
        "uid": "AP0002",
        "email": "tracy@example.com",
        "username": "tracy",
        "position": "小组长",
        "shop": 1024,
        "salary": 5000.0,
        "time": 0,
        "workingDay": "1,3,4",
        "workingHours": "08:00-12:00,18:00-22:00",
        "durationOfShift": 4,
        "durationOfWeek": 20
      }
    ]
  }
}
```
```json
{
  "code": 0,
  "message": "获取员工失败",
  "data": null
}
```

## 更新员工信息
更新员工信息。

获取时，发送请求如下

POST请求 /api/employee/update
```yaml
id: Long （必填）员工id
position: String （可选）员工职位，可选值：门店经理，副经理，小组长，收银，导购，库房
shop: Long （可选）所属门店id
salary: Double （可选）薪资
```
```json
{
  "code": 0,
  "message": "更新员工成功",
  "data": null
}
```
```json
{
  "code": 0,
  "message": "更新员工失败",
  "data": null
}
```