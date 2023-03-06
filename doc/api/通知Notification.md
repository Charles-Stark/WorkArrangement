# API - 通知系统相关

## 通知类的定义

```Notification```类根据其```type(Integer)```属性，分为不同类型。不同类型的通知对象之间在属性的含义上的差异仅存在于```type```和```text```.

```yaml
id: Long 通知的id（自增主键，唯一标识）
isRead: Boolean 是否已读（true为已读）
fromUser: Long 通知来源（用户id）
toUser: Long 通知去向（用户id）
type: Integer 通知类型
text: String 通知数据（根据类型不同在形式上有变化）
createAt: Date 创建时间
```
有新的排班表产生：
```yaml
type: 1
text: 1024  # 新排班表的id
```
排班表发生变更：
```yaml
type: 2
text: 1025  # 发生变更的排班表id
```
开放班次待认领：
```yaml
type: 3
text: 
```

