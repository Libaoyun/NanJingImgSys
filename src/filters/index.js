import Vue from 'vue'

// 转化文件大小
export function formatSize(val) {
  if (val === 0) return "0 B";
  var k = 1024;
  var sizes = ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"];
  var i = Math.floor(Math.log(val) / Math.log(k));
  return fomatFloat(val / Math.pow(k, i), 2) + " " + sizes[i];
}

// 保留两位小数 浮点数四舍五入 位数不够 不补0
function fomatFloat(src, pos) {
  return Math.round(src * Math.pow(10, pos)) / Math.pow(10, pos);
}

// 转化日期
export function formatDate(time) {
  if (!time){
    return '——'
  }else{
    var date = Vue.prototype.$moment(time).format('YYYY-MM-DD HH:mm:ss');
    return date;                        
  }
}
