import Vue from "vue";
import Component from "vue-class-component";

@Component({})
export default class rule extends Vue {
    //保留两位小数
    digitPattern = /^\d+\.?\d{0,2}$/;
    numPattern = /^\d{0,9}$/;//^[0-9]*$
    taxPattern = /^\d{0,2}$/;
    phonePattern = /^\d{11}$/; //联系电话只校验数字&长度
    emailPattern = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;//邮箱校验

    created() {}
    //基本信息表单验证
    formRules = {
        projectName: [
            { required: true, message: '请输入项目名称', trigger: 'change' }
        ],
        unitName: [
            { required: true, message: '请输入项目部公章全称', trigger: 'change' }
        ],
        unitAddress: [
            { required: true, message: '请输入项目部所在地址', trigger: 'change' }
        ],
        applyUserName: [
            { required: true, message: '点击弹出用户列表选择申请人', trigger: 'change' }
        ],
        genderCode: [
            { required: true, message: '请选择性别', trigger: 'change' }
        ],
        age: [
            { required: true, message: '请输入年龄', trigger: 'change' },
            {
                pattern: this.numPattern,
                message: "年龄只能为数值",
                trigger: "blur",
            }
        ],
        postCode: [
            { required: true, message: '青选择职务', trigger: 'change' }
        ],
        telephone: [
            { required: true, message: '请输入电话号码', trigger: 'change' },
            {
                pattern: this.phonePattern,
                message: "请输入正确的电话号码",
                trigger: "change"
            }
        ],
        applyAmount: [
            { required: true, message: '请输入申请经费', trigger: 'change' },
            {
                pattern: this.digitPattern,
                message: "申请经费只能为数值并保留两位小数",
                trigger: "change"
            }
        ],
        startYear: [
            { required: true, message: '请选择起始年度的1月1日为起始年度', trigger: 'change' }
        ],
        endYear: [
            { required: true, message: '请选择结束年度的12月31日为结束年度', trigger: 'change' }
        ],
        zipCode: [
            { required: true, message: '请输入邮编', trigger: 'change' }
        ],
        professionalCategoryCode: [
            { required: true, message: '请选择专业类别', trigger: 'change' }
        ],
        projectTypeCode: [
            { required: true, message: '请选择项目类型', trigger: 'change' }
        ],
        identify: [
            { required: true, message: '请选择是否进行成果鉴定', trigger: 'change' }
        ],
        researchContents: [
            { required: true, message: '请输入研究内容题要', trigger: 'change' },
            {
                min: 200,
                message: "内容不得少于200字",
                trigger: "blur"
            }
        ],
        reviewComments: [
            { required: true, message: '请输入申报单位审查意见', trigger: 'change' },
            {
                min: 200,
                message: "内容不得少于200字",
                trigger: "blur"
            }
        ],
        currentSituation: [
            { required: true, message: '请输入国内外现状', trigger: 'change' },
            {
                min: 200,
                message: "内容不得少于200字",
                trigger: "blur"
            }
        ],
        purposeSignificance: [
            { required: true, message: '请输入研发目的和意义', trigger: 'change' },
            {
                min: 200,
                message: "内容不得少于200字",
                trigger: "blur"
            }
        ],
        contentMethod: [
            { required: true, message: '请输入主要研究内容及研究方法', trigger: 'change' },
            {
                min: 200,
                message: "内容不得少于200字",
                trigger: "blur"
            }
        ],
        targetResults: [
            { required: true, message: '请输入要达到的目标、成果形式及主要技术指标', trigger: 'change' },
            {
                min: 200,
                message: "内容不得少于200字",
                trigger: "blur"
            }
        ],
        basicConditions: [
            { required: true, message: '请输入现有研发条件和工作基础', trigger: 'change' },
            {
                min: 200,
                message: "内容不得少于200字",
                trigger: "blur"
            }
        ],
        innovationPoints: [
            { required: true, message: '请输入研发项目创新点', trigger: 'change' },
            {
                min: 200,
                message: "内容不得少于200字",
                trigger: "blur"
            }
        ],
        feasibilityAnalysis: [
            { required: true, message: '请输入成果转化的可行性分析', trigger: 'change' },
            {
                min: 200,
                message: "内容不得少于200字",
                trigger: "blur"
            }
        ],
    };
    progressRules = {
        years: [
            { required: true, message: '请输入年度', trigger: 'change' }
        ],
        planTarget: [
            { required: true, message: '请输入计划及目标', trigger: 'change' }
        ],
    }
    partUnitRules = {
        unitName: [
            { required: true, message: '请输入参加单位', trigger: 'change' }
        ],
        taskDivision: [
            { required: true, message: '请输入研究任务及分工', trigger: 'change' }
        ],
    }
    researchersRules = {
        userName: [
            { required: true, message: '', trigger: 'change' }
        ],
        idCard: [
            { required: true, message: '', trigger: 'change' }
        ],
        age: [
            { required: true, message: '', trigger: 'change' }
        ],
        gender: [
            { required: true, message: '', trigger: 'change' }
        ],
        education: [
            { required: true, message: '', trigger: 'change' }
        ],
        belongDepartment: [
            { required: true, message: '', trigger: 'change' }
        ],
        belongPost: [
            { required: true, message: '', trigger: 'change' }
        ],
        majorStudied: [
            { required: true, message: '', trigger: 'change' }
        ],
        majorWorked: [
            { required: true, message: '', trigger: 'change' }
        ],
        belongUnit: [
            { required: true, message: '', trigger: 'change' }
        ],
        taskDivision: [
            { required: true, message: '', trigger: 'change' }
        ],
        workRate: [
            { required: true, message: '', trigger: 'change' }
        ],
        telephone: [
            { required: true, message: '', trigger: 'change' }
        ],
        startDate: [
            { required: true, message: '', trigger: 'change' }
        ],
        endDate: [
            { required: true, message: '', trigger: 'change' }
        ],
    }
    budgetRules = {
        sourceBudget: [
            { required: true, message: '', trigger: 'change' }
        ],
        expenseBudget: [
            { required: true, message: '', trigger: 'change' }
        ],
    }
    allocationRules = {
        years: [
            { required: true, message: '', trigger: 'change' }
        ],
        planAmount: [
            { required: true, message: '', trigger: 'change' }
        ],
    }
}