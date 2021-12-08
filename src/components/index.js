import cardGlobal from './cardGlobal.vue'
import imgPreview from './imgPreview.vue'
import uploadApprovalGlobal from './uploadApprovalGlobal.vue'
import approvalGlobal from './approvalGlobal.vue'
import loadingBtn from './loadingBtn.vue'
import footerGlobal from './footerGlobal.vue'
import uploadAttachment from './uploadAttachment.vue'

export const CardGlobal = {
    install:function(Vue){
        Vue.component('CardGlobal', cardGlobal)
    }
}

export const ImgPreview = {
    install:function(Vue){
        Vue.component('ImgPreview', imgPreview)
    }
}

export const UploadApprovalGlobal = {
    install:function(Vue){
        Vue.component('UploadApprovalGlobal',uploadApprovalGlobal)
    }
}

export const ApprovalGlobal = {
    install:function(Vue){
        Vue.component('ApprovalGlobal',approvalGlobal)
    }
}

export const LoadingBtn = {
    install:function(Vue){
        Vue.component('LoadingBtn',loadingBtn)
    }
}

export const FooterGlobal = {
    install:function(Vue){
        Vue.component('FooterGlobal', footerGlobal)
    }
}

export const UploadAttachment = {
    install:function(Vue){
        Vue.component('UploadAttachment', uploadAttachment)
    }
}