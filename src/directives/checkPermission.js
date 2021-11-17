export const checkPermission = {
    inserted: function (el, binding, vnode) {
        let btnPermissions = vnode.context.$route.meta.btnPermissions || [];
        let flag = btnPermissions.some(item=>{
            return item.menuButtonCode === binding.value
        })
        !flag &&  el.parentNode.removeChild(el);
    }
}