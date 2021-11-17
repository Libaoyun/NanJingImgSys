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