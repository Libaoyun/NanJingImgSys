import Vue from 'vue';
import  Component  from 'vue-class-component';

@Component  // 一定要用Component修饰
export default class dictionaryMixin extends Vue {
    genderList = []
    educationList = []
    bloodTypeList = []
    employeeStatusList = []
    employeeTypeList = []


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
        this.GET_DICTIONARY_LIST(1015).then(res=>{
            this.employeeTypeList = res;
        })
    }
}
