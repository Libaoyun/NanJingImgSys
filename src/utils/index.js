export function checkForm(_self, formName) {
    return new Promise(function(resolve, reject) {
      _self.$refs[formName].validate((valid) => {
          if (valid) {
            resolve();
          } else {
            reject() 
            return false
            }
        })
    })
}

export function getUrlKey(name) {
  return (
    decodeURIComponent(
      (new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(
        location.href
      ) || [, ''])[1].replace(/\+/g, '%20')
    ) || null
  );
}

export function jsonToFormData (json) {
  const form = new FormData()
  const keys = Object.keys(json)
  
  keys.forEach(key => {
    form.append(key, typeof json[key] === 'object' ? JSON.stringify(json[key]) : json[key])
  })

  return form
}

// 保留两位小数 浮点数四舍五入 位数不够 补0
export function fomatFloat(src, pos = 2) {
  return (Math.round(src * Math.pow(10, pos)) / Math.pow(10, pos)).toFixed(2);
}

// 获取唯一的uuid
export function getUid() {
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
		var r = Math.random() * 16 | 0,
			v = c == 'x' ? r : (r & 0x3 | 0x8);
		return v.toString(16);
	});
}

// 转化日期2020-01-19 11:09:49
export function formatTimeDate(time, fmt) {
  if (!time) return "";
  let date = new Date(time) || "";
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(
      RegExp.$1,
      (date.getFullYear() + "").substr(4 - RegExp.$1.length)
    );
  }
  let o = {
    "M+": date.getMonth() + 1,
    "d+": date.getDate(),
    "H+": date.getHours(),
    "m+": date.getMinutes(),
    "s+": date.getSeconds(),
  };
  for (let k in o) {
    if (new RegExp(`(${k})`).test(fmt)) {
      let str = o[k] + "";
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length === 1 ? str : padLeftZero(str)
      );
    }
  }
  return fmt;
}
function padLeftZero(str) {
  return ("00" + str).substr(str.length);
}