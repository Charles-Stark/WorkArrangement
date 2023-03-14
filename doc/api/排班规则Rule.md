# API - 排班规则相关

## 添加排班规则并创建排班

管理员用户在排班<del>前</del>时，为门店设置排班规则，这将会在 ``排班规则Rule数据表``生成一条记录，同时在 ``排班Schedule数据表``依此规则新建排班。

请求参数可参考思维导图。

POST请求 /api/rule/add

```yaml
shop: long 门店id
manager: long 管理员id
prepareTime: double 准备时间
prepareWorkloadPerPerson: double 人均单位工作量
preparePosition: string 准备时限制职位(可选)
maxServiceNumber: double 员工最大服务人数
servicePosition: string 限制职位(可选)
numberOnDuty: int 值班人数
closingTime: double 收尾时间
closingWorkloadPerPersonU: double 人均收尾工作量u
closingWorkloadPerPersonV: double 人均收尾工作量v
closingPosition: string 收尾时限制职位(可选)
startDate: Date 开始日期 格式：yyyy-MM-dd HH:mm:ss, 例：2023-03-12 00:00:00
lastingDays: int 排班表持续时间
```

```json
{
  "code": 0,
  "message": "添加规则成功",
  "data": {
    "id": 5,
    "shop": 1,
    "prepareTime": 1.5,
    "prepareWorkloadPerPerson": 100.0,
    "preparePosition": null,
    "maxServiceNumber": 3.8,
    "servicePosition": null,
    "numberOnDuty": 1,
    "closingTime": 2.0,
    "closingWorkloadPerPersonU": 80.0,
    "closingWorkloadPerPersonV": 1.0,
    "closingPosition": null
  }
}
```

```json
{
  "code": -1,
  "message": "添加规则失败",
  "data": null
}
```

## 删除排班规则

删除排班规则。

POST请求 /api/rule/delete

```yaml
id: long 待删除的规则id
```

```json
{
  "code": 0,
  "message": "删除排班规则成功",
  "data": null
}
```

```json
{
  "code": -1,
  "message": "删除排班规则失败",
  "data": null
}
```

## 获取排班规则

获取排班规则。

GET请求 /api/rule/get/{id}

```yaml
id: long 规则id
```

```json
{
  "code": 0,
  "message": "获取排班规则成功",
  "data": {
    "id": 2,
    "shop": 1,
    "prepareTime": 1.5,
    "prepareWorkloadPerPerson": 100.0,
    "preparePosition": null,
    "maxServiceNumber": 3.8,
    "servicePosition": null,
    "numberOnDuty": 1,
    "closingTime": 2.0,
    "closingWorkloadPerPersonU": 80.0,
    "closingWorkloadPerPersonV": 1.0,
    "closingPosition": null
  }
}
```

```json
{
  "code": -1,
  "message": "获取排班规则失败",
  "data": null
}
```

## 获取指定门店的所有排班规则

获取指定门店的所有排班规则。

GET请求 /api/rule/getAll/{shopId}

```yaml
shopId: long 门店id
```

```json
{
  "code": 0,
  "message": "获取排班规则成功",
  "data": [
    {
      "id": 2,
      "shop": 1,
      "prepareTime": 1.5,
      "prepareWorkloadPerPerson": 100.0,
      "preparePosition": null,
      "maxServiceNumber": 3.8,
      "servicePosition": null,
      "numberOnDuty": 1,
      "closingTime": 2.0,
      "closingWorkloadPerPersonU": 80.0,
      "closingWorkloadPerPersonV": 1.0,
      "closingPosition": null
    },
    {
      "id": 5,
      "shop": 1,
      "prepareTime": 1.5,
      "prepareWorkloadPerPerson": 100.0,
      "preparePosition": null,
      "maxServiceNumber": 3.8,
      "servicePosition": null,
      "numberOnDuty": 1,
      "closingTime": 2.0,
      "closingWorkloadPerPersonU": 80.0,
      "closingWorkloadPerPersonV": 1.0,
      "closingPosition": null
    }
  ]
}
```

```json
{
  "code": -1,
  "message": "获取排班规则失败",
  "data": null
}
```
