
export function formatDate(value) { // 时间戳转换日期格式方法
    value=parseInt(value)
    if (value == null) {
        return ''
    } else {
        const date = new Date(value)
        const y = date.getFullYear()// 年
        let MM = date.getMonth() + 1 // 月
        MM = MM < 10 ? ('0' + MM) : MM
        let d = date.getDate() // 日
        d = d < 10 ? ('0' + d) : d
        return y + '-' + MM + '-' + d
    }
}


export function formatTime(value) {     // 时间戳转换日期格式方法
    value=parseInt(value)
    if (value == null) {
        return ''
    } else {
        const date = new Date(value)
        let h = date.getHours()// 小时
        let m = date.getMinutes() // 分
        if(h<10) h='0'+h
        if(m<10) m='0'+m
        return h + ':'+m
    }
}
