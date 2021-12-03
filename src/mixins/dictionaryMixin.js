import Vue from 'vue';
import  Component  from 'vue-class-component';

@Component  // 一定要用Component修饰
export default class dictionaryMixin extends Vue {
    operateTypeList = []
    genderList = []
    educationList = []
    bloodTypeList = []
    employeeStatusList = []
    employeeTypeList = []
    approvalStatusList = []
    approvalResultList = []
    projectTypeList = []
    professionalCategroyList = []


    // 获取操作类型
    getOperateTypeList(){
        this.GET_DICTIONARY_LIST(1011).then(res=>{
            this.operateTypeList = res;
        })
    }
    // 获取性别
    getGenderList(){
        this.GET_DICTIONARY_LIST(1012).then(res=>{
            this.genderList = res;
        })
    }
    // 获取学历
    getEducationList(){
        this.GET_DICTIONARY_LIST(1013).then(res=>{
            this.educationList = res;
        })
    }
    // 获取血型
    getBloodTypeList(){
        this.GET_DICTIONARY_LIST(1014).then(res=>{
            this.bloodTypeList = res;
        })
    }
    // 获取员工状态
    getEmployeeStatusList(){
        this.GET_DICTIONARY_LIST(1015).then(res=>{
            this.employeeStatusList = res;
        })
    }
    // 获取员工类型
    getEmployeeTypeList(){
        this.GET_DICTIONARY_LIST(1016).then(res=>{
            this.employeeTypeList = res;
        })
    }
    // 获取审批状态
    getApprovalStatusList(){
        this.GET_DICTIONARY_LIST(1017).then(res=>{
            this.approvalStatusList = res;
        })
    }
    // 获取审批处理结果
    getApprovalResultList(){
        this.GET_DICTIONARY_LIST(1018).then(res=>{
            this.approvalResultList = res;
        })
    }
    // 获取项目类型
    getProjectTypeList(){
        this.GET_DICTIONARY_LIST(1019).then(res=>{
            this.projectTypeList = res;
        })
    }
    // 获取专业类别
    getProfessionalCategroyList(){
        this.GET_DICTIONARY_LIST(1020).then(res=>{
            this.professionalCategroyList = res;
        })
    }
}
