<template>
    <div style="padding-bottom: 46px">
        <!-- 主信息 -->
        <card-global cardTitle="主信息">
            <el-form ref="doForm" :inline="true" :model="baseInfo" size="mini" label-position="right" label-width="80px">
                <el-form-item label="申请单号:" prop="serialNumber">
                    <span>{{baseInfo.serialNumber}}</span>
                </el-form-item>
                <el-form-item label="编制人:" prop="creatorUserName">
                    <span>{{baseInfo.creatorUserName}}</span>
                </el-form-item>
                <el-form-item label="编制日期:" prop="createdDate">
                    <span>{{baseInfo.createdDate}}</span>
                </el-form-item>
                <el-form-item label="项目名称:" prop="projectName">
                    <span>{{baseInfo.projectName}}</span>
                </el-form-item>
                <el-form-item label="单位名称:" prop="unitName">
                    <span>{{baseInfo.unitName}}</span>
                </el-form-item>
                <el-form-item label="单位地址:" prop="unitAddress">
                    <span>{{baseInfo.unitAddress}}</span>
                </el-form-item>
                <el-form-item label="申请人:" prop="applyUserName">
                    <span>{{baseInfo.applyUserName}}</span>
                </el-form-item>
                <el-form-item label="性别:" prop="genderCode">
                    <span>{{baseInfo.gender}}</span>
                </el-form-item>
                <el-form-item label="年龄:" prop="age">
                    <span>{{baseInfo.age}}</span>
                </el-form-item>
                <el-form-item label="职务:" prop="postName">
                    <span>{{baseInfo.postName}}</span>
                    
                </el-form-item>
                <el-form-item label="电话号码:" prop="telephone">
                    <span>{{baseInfo.telephone}}</span>
                </el-form-item>
                <el-form-item label="申请经费:" prop="applyAmount">
                    <span>{{baseInfo.applyAmount}} (万元)</span>
                </el-form-item>
                <el-form-item label="起始年度:" prop="startYear">
                    <span>{{baseInfo.startYear}}</span>
                </el-form-item>
                <el-form-item label="结束年度:" prop="endYear">
                    <span>{{baseInfo.endYear}}</span>
                </el-form-item>
                <el-form-item label="邮编:" prop="zipCode">
                    <span>{{baseInfo.zipCode}}</span>
                </el-form-item>
                <el-form-item label="专业类别:" prop="professionalCategoryCode">
                    <span>{{baseInfo.professionalCategory}}</span>
                </el-form-item>
                <el-form-item label="项目类型:" prop="projectTypeCode">
                    <span>{{baseInfo.projectType}}</span>
                </el-form-item>
                <el-form-item label="是否鉴定:" prop="identify">
                    <span>{{['否','是'][baseInfo.identify]}}</span>
                </el-form-item>
                <el-form-item label="研究内容题要:" prop="researchContents" class="large">
                    <span style="word-break:break-all">{{baseInfo.researchContents}}</span>
                </el-form-item>
                <el-form-item label="申报单位审查意见:" prop="reviewComments" class="large">
                    <span style="word-break:break-all">{{baseInfo.reviewComments}}</span>
                </el-form-item>
                <el-form-item label="申报单位盖章:" prop="val177" class="large inline">
                    <el-input readonly type="textarea" v-model="baseInfo.val177" minlength="200" rows="5" resize="none" placeholder="盖章处"></el-input>
                </el-form-item>
            </el-form>
        </card-global>
        <!-- 立项调研信息 -->
        <card-global cardTitle="立项调研信息">
            <el-form ref="infoForm" :inline="true" :model="baseInfo" size="mini" label-position="right" label-width="80px">
                <el-form-item label="一、国内外现状:" prop="currentSituation" class="large">
                    <span style="word-break:break-all">{{baseInfo.currentSituation}}</span>
                </el-form-item>
                <el-form-item label="二、研发目的和意义:" prop="purposeSignificance" class="large">
                    <span style="word-break:break-all">{{baseInfo.purposeSignificance}}</span>
                </el-form-item>
                <el-form-item label="三、主要研究内容及研究方法:" prop="contentMethod" class="large">
                    <span style="word-break:break-all">{{baseInfo.contentMethod}}</span>
                </el-form-item>
                <el-form-item label="四、要达到的目标、成果形式及主要技术指标:" prop="targetResults" class="large">
                    <span style="word-break:break-all">{{baseInfo.targetResults}}</span>
                </el-form-item>
                <el-form-item label="五、现有研发条件和工作基础:" prop="basicConditions" class="large">
                    <span style="word-break:break-all">{{baseInfo.basicConditions}}</span>
                </el-form-item>
                <el-form-item label="六、研发项目创新点:" prop="innovationPoints" class="large">
                    <span style="word-break:break-all">{{baseInfo.innovationPoints}}</span>
                </el-form-item>
                <el-form-item label="七、成果转化的可行性分析:" prop="feasibilityAnalysis" class="large">
                    <span style="word-break:break-all">{{baseInfo.feasibilityAnalysis}}</span>
                </el-form-item>
            </el-form>
        </card-global>
        <!-- 研发项目立项申请-进度计划 -->
        <card-global cardTitle="研发项目立项申请-进度计划" type='table'>
            <el-form ref="progressForm" :inline="true" :model="baseInfo.detailForm" size="mini" :show-message="false">
                <el-table
                    :data="baseInfo.detailForm.progressPlan"
                    :border="tableConfig.border"
                    class="global-table-default"
                    style="width: 100%">
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="years" label="年度" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'progressPlan.'+scope.$index+'.years'">
                                <el-date-picker v-model="scope.row.years" type="year" format="yyyy"></el-date-picker>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="planTarget" label="计划及目标" width="300" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'progressPlan.'+scope.$index+'.planTarget'">
                                <span>{{scope.row.planTarget}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="creatorUser" label="编制人" width="80" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'progressPlan.'+scope.$index+'.creatorUser'">
                                <span>{{scope.row.creatorUser}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="编制时间" width="180" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'progressPlan.'+scope.$index+'.createTime'">
                                <span>{{scope.row.createTime}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
        </card-global>
        <!-- 研发项目立项申请-参加单位 -->
        <card-global cardTitle="研发项目立项申请-参加单位" type="table">
            <el-form ref="partUnitForm" :inline="true" :model="baseInfo.detailForm" size="mini" :show-message="false">
                <el-table
                    :data="baseInfo.detailForm.attendUnit"
                    :border="tableConfig.border"
                    class="global-table-default"
                    style="width: 100%">
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="unitName" label="参加单位" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'attendUnit.'+scope.$index+'.unitName'">
                                <span>{{scope.row.unitName}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="taskDivision" label="研究任务及分工" width="300" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'attendUnit.'+scope.$index+'.taskDivision'">
                                <span>{{scope.row.taskDivision}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="creatorUser" label="编制人" width="80" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'attendUnit.'+scope.$index+'.creatorUser'">
                                <span>{{scope.row.creatorUser}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="编制时间" width="180" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'attendUnit.'+scope.$index+'.createTime'">
                                <span>{{scope.row.createTime}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
        </card-global>
        <!-- 研发项目立项申请-研究人员（初始） -->
        <card-global cardTitle="研发项目立项申请-研究人员（初始）" type="table">
            <el-form ref="researchersForm" :inline="true" :model="baseInfo.detailForm" size="mini" :show-message="false">
                <el-table
                    :data="baseInfo.detailForm.researchUser"
                    :border="tableConfig.border"
                    class="global-table-default"
                    style="width: 100%">
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="userName" label="姓名" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.userName'">
                                <span>{{scope.row.userName}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="idCard" label="身份证号码" width="160" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.idCard'">
                                <span>{{scope.row.idCard}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="age" label="年龄" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.age'">
                                <span>{{scope.row.age}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="gender" label="性别" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.gender'">
                                <span>{{scope.row.gender}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="education" label="学历" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.education'">
                                <span>{{scope.row.education}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="belongDepartment" label="所属部门" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.belongDepartment'">
                                <span>{{scope.row.belongDepartment}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="belongPost" label="职务职称" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.belongPost'">
                                <span>{{scope.row.belongPost}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="majorStudied" label="所学专业" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.majorStudied'">
                                <span>{{scope.row.majorStudied}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="majorWorked" label="现从事专业" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.majorWorked'">
                                <span>{{scope.row.majorWorked}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="belongUnit" label="所在单位" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.belongUnit'">
                                <span>{{scope.row.belongUnit}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="taskDivision" label="研究任务及分工" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.taskDivision'">
                                <span>{{scope.row.taskDivision}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="workRate" label="全时率" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.workRate'">
                                <span>{{scope.row.workRate}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="telephone" label="联系电话" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.telephone'">
                                <span>{{scope.row.telephone}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="startDate" label="参与研发开始日期" width="160" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.startDate'">
                                <span>{{scope.row.startDate}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="endDate" label="参与研发结束日期" width="160" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.endDate'">
                                <span>{{scope.row.endDate}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="creatorUser" label="编制人" width="80" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.creatorUser'">
                                <span>{{scope.row.creatorUser}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="编制时间" width="180" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.createTime'">
                                <span>{{scope.row.createTime}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
        </card-global>
        <!-- 研发项目立项申请-研究人员（变更） -->
        <card-global cardTitle="研发项目立项申请-研究人员（变更）" type="table" v-if="baseInfo.detailForm && baseInfo.detailForm.researchUserChange.length > 0">
            <el-form ref="researchUserChangeForm" :inline="true" :model="baseInfo.detailForm" size="mini" :show-message="false">
                <el-table
                    :data="baseInfo.detailForm.researchUserChange"
                    :border="tableConfig.border"
                    class="global-table-default"
                    style="width: 100%">
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="userStatus" label="状态" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.userStatus'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.userStatus=='1'?'正常':'调离'}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="userName" label="姓名" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.userName'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.userName}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="idCard" label="身份证号码" width="160" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.idCard'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.idCard}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="age" label="年龄" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.age'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.age}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="gender" label="性别" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.gender'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.gender}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="education" label="学历" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.education'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.education}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="belongDepartment" label="所属部门" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.belongDepartment'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.belongDepartment}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="belongPost" label="职务职称" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.belongPost'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.belongPost}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="majorStudied" label="所学专业" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.majorStudied'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.majorStudied}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="majorWorked" label="现从事专业" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.majorWorked'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.majorWorked}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="belongUnit" label="所在单位" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.belongUnit'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.belongUnit}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="taskDivision" label="研究任务及分工" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.taskDivision'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.taskDivision}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="workRate" label="全时率" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.workRate'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.workRate}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="telephone" label="联系电话" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.telephone'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.telephone}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="startDate" label="参与研发开始日期" width="160" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.startDate'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.startDate}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="endDate" label="参与研发结束日期" width="160" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.endDate'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.endDate}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="creatorUser" label="编制人" width="80" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.creatorUser'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.creatorUser}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="编制时间" width="180" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUserChange.'+scope.$index+'.createTime'">
                                <span :class="{'redClass':scope.row.userStatus=='1','greeClass':scope.row.userStatus=='2'}">{{scope.row.createTime}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
        </card-global>
        <!-- 研发项目立项申请-经费预算 -->
        <card-global cardTitle="研发项目立项申请-经费预算" type="table">
            <el-form ref="budgetForm" :inline="true" :model="baseInfo.detailForm" size="mini" :show-message="false">
                <el-table
                    :data="baseInfo.detailForm.budgetList"
                    :border="tableConfig.border"
                    class="global-table-default"
                    style="width: 100%">
                    <el-table-column label="经费来源预算" align="center">
                        <el-table-column prop="sourceAccount" label="科目" align="center">
                            <template slot-scope="scope">
                                <el-form-item :prop="'budgetList.'+scope.$index+'.sourceAccount'">
                                    <span>{{scope.row.sourceAccount}}</span>
                                </el-form-item>
                            </template>
                        </el-table-column>
                        <el-table-column prop="sourceBudget" :label="isSourceBudgetChange?'初始预算数 (万元)':'预算数 (万元)'" align="center">
                            <template slot-scope="scope">
                                <el-form-item v-if="scope.$index<=6" :prop="'budgetList.'+scope.$index+'.sourceBudget'">
                                    <span>{{scope.row.sourceBudget}}</span>
                                </el-form-item>
                            </template>
                        </el-table-column>
                        <el-table-column prop="sourceBudgetChange" label="变更后预算数 (万元)" align="center" v-if="isSourceBudgetChange">
                            <template slot-scope="scope">
                                <el-form-item v-if="scope.$index<=6" :prop="'budgetList.'+scope.$index+'.sourceBudgetChange'">
                                    <span>{{scope.row.sourceBudgetChange}}</span>
                                </el-form-item>
                            </template>
                        </el-table-column>
                    </el-table-column>
                    
                    <el-table-column label="经费支出预算" align="center">
                        <el-table-column prop="expenseAccount" label="科目" align="center">
                            <template slot-scope="scope">
                                <el-form-item :prop="'budgetList.'+scope.$index+'.expenseAccount'">
                                    <span>{{scope.row.expenseAccount}}</span>
                                </el-form-item>
                            </template>
                        </el-table-column>
                        <el-table-column prop="expenseBudget" :label="isExpenseBudgetChange?'初始预算数 (万元)':'预算数 (万元)'" align="center">
                            <template slot-scope="scope">
                                <el-form-item :prop="'budgetList.'+scope.$index+'.expenseBudget'">
                                    <span>{{scope.row.expenseBudget}}</span>
                                </el-form-item>
                            </template>
                        </el-table-column>
                        <el-table-column prop="expenseBudgetChange" label="变更后预算数 (万元)" align="center" v-if="isExpenseBudgetChange">
                            <template slot-scope="scope">
                                <el-form-item :prop="'budgetList.'+scope.$index+'.expenseBudgetChange'">
                                    <span>{{scope.row.expenseBudgetChange}}</span>
                                </el-form-item>
                            </template>
                        </el-table-column>
                    </el-table-column>
                </el-table>
            </el-form>
        </card-global>
        <!-- 研发项目立项申请-经费预算（每月预算） -->
        <card-global cardTitle="研发项目立项申请-经费预算（每月预算）" type="table">
            <el-form ref="everyMonthForm" :inline="true" :model="baseInfo.detailForm" size="mini" :show-message="false">
                <el-table
                    :data="baseInfo.detailForm.monthList"
                    :border="tableConfig.border"
                    class="global-table-default"
                    style="width: 100%">
                    <el-table-column label="经费来源预算" align="center">
                        <el-table-column prop="sourceaccount" label="科目" align="center" width="180">
                            <template slot-scope="scope">
                                <el-form-item :prop="'monthList.'+scope.$index+'.sourceaccount'">
                                    <span>{{scope.row.sourceaccount}}</span>
                                </el-form-item>
                            </template>
                        </el-table-column>
                        <el-table-column prop="sourcebudget" label="预算数 (万元)" align="center" width="160">
                            <template slot-scope="scope">
                                <el-form-item v-if="scope.$index<=6" :prop="'monthList.'+scope.$index+'.sourcebudget'">
                                    <span>{{scope.row.sourcebudget}}</span>
                                </el-form-item>
                            </template>
                        </el-table-column>
                    </el-table-column>
                    
                    <el-table-column label="经费支出预算" align="center">
                        <el-table-column prop="expenseaccount" label="科目" align="center" width="180">
                            <template slot-scope="scope">
                                <el-form-item :prop="'monthList.'+scope.$index+'.expenseaccount'">
                                    <span>{{scope.row.expenseaccount}}</span>
                                </el-form-item>
                            </template>
                        </el-table-column>
                        <el-table-column prop="expensebudget" label="预算数 (万元)" align="center" width="160">
                            <template slot-scope="scope">
                                <el-form-item :prop="'monthList.'+scope.$index+'.expensebudget'">
                                    <span>{{scope.row.expensebudget}}</span>
                                </el-form-item>
                            </template>
                        </el-table-column>
                    </el-table-column>
                    <el-table-column label="年度预算 (按月填报)" align="center">
                        <el-table-column v-for="item in yms" :key="item.years" :label="item.years+'年'" align="center">
                            <el-table-column v-for="c in item.months" :key="'month'+item.years+c" :prop="c" :label="c+'月'" align="center" width="80">
                                <template slot-scope="scope">
                                    <el-form-item>
                                        <span>{{scope.row['month'+item.years+c]}}</span>
                                    </el-form-item>
                                </template>
                            </el-table-column>
                        </el-table-column>
                    </el-table-column>
                </el-table>
            </el-form>
        </card-global>
        <!-- 研发项目立项申请-拨款计划 -->
        <card-global cardTitle="研发项目立项申请-拨款计划" type="table">
            <el-form ref="allocationForm" :inline="true" :model="baseInfo.detailForm" size="mini" :show-message="false">
                <el-table
                    :data="baseInfo.detailForm.appropriationPlan"
                    :border="tableConfig.border"
                    class="global-table-default"
                    style="width: 100%">
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="years" label="年度" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'appropriationPlan.'+scope.$index+'.years'">
                                <span>{{scope.row.years}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="planAmount" label="计划 (万元)" width="300" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'appropriationPlan.'+scope.$index+'.planAmount'">
                                <span>{{scope.row.planAmount}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="creatorUser" label="编制人" width="80" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'appropriationPlan.'+scope.$index+'.creatorUser'">
                                <span>{{scope.row.creatorUser}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="编制时间" width="180" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'appropriationPlan.'+scope.$index+'.createTime'">
                                <span>{{scope.row.createTime}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
        </card-global>
        <approval-global ref="approval" :processInstId="processInstId" :serialNumber="serialNumber" v-if="processInstId"></approval-global>
        <div class="global-fixBottom-actionBtn">
            <el-button size="mini" @click="backBtn">返回</el-button>
            <loading-btn class="primary" size="mini" @click="resolve(1)" type="primary" :loading="loadingBtn">同意</loading-btn>
            <loading-btn class="rejectOrigin" size="mini" @click="rejectOrigin(2)" :loading="loadingBtn">退回发起人</loading-btn>
            <loading-btn size="mini" @click="rejectPre(3)" :loading="loadingBtn">退回上节点</loading-btn>
        </div>
    </div>
</template>

<script>
import { Component, Mixins, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import CardGlobal from '../../../../components/cardGlobal.vue';

@Component({
    name: 'setProjectApplyApproval',
    components: {
        CardGlobal,
    }
})
   
export default class extends tableMixin {
    baseInfo = this.getBaseInfo()
    yms = []
    isSourceBudgetChange = false
    isExpenseBudgetChange = false
    loadingBtn = 0
    processInstId = null
    serialNumber = null
    // 设置空数据
    getBaseInfo(){
       return {
            creatorUserName:this.$store.getters.userInfo?.userName,
            creatorUserId:this.$store.getters.userInfo?.userCode,
            createdDate: "",
            projectName:"",
            unitName:"",
            unitAddress:"",
            applyUserName:"",
            applyUserId:"",
            genderCode:"",
            gender:"",
            age:"",
            postCode:"",
            postName:"",
            telephone:"",
            applyAmount:"",
            startYear:"",
            endYear:"",
            zipCode:"",
            professionalCategoryCode:"",
            professionalCategory:"",
            projectTypeCode:"",
            projectType:"",
            identify:"",
            researchContents:"",
            reviewComments:"",
            val177:"",
            currentSituation:"",
            purposeSignificance:"",
            contentMethod:"",
            targetResults:"",
            basicConditions:"",
            innovationPoints:"",
            feasibilityAnalysis:"",
            detailForm:{
                progressPlan:[],
                attendUnit:[],
                researchUser:[],
                researchUserChange:[],
                // budgetList:[
                //     {
                //         sourceaccount:'来源预算合计',
                //         sourcebudget:'',
                //         expenseaccount:'支出预算合计',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'一、股份公司计划拨款',
                //         sourcebudget:'',
                //         expenseaccount:'一、人员费',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'二、国家拨款',
                //         sourcebudget:'',
                //         expenseaccount:'二、设备费',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'三、省市拨款',
                //         sourcebudget:'',
                //         expenseaccount:'三、材料费',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'四、单位自筹款',
                //         sourcebudget:'',
                //         expenseaccount:'四、燃料及动力费',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'五、银行贷款',
                //         sourcebudget:'',
                //         expenseaccount:'五、测试及化验费',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'六、其他来源款',
                //         sourcebudget:'',
                //         expenseaccount:'六、差旅费',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'',
                //         sourcebudget:'',
                //         expenseaccount:'七、会议费',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'',
                //         sourcebudget:'',
                //         expenseaccount:'八、课题管理费',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'',
                //         sourcebudget:'',
                //         expenseaccount:'九、其他费用',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'',
                //         sourcebudget:'',
                //         expenseaccount:'1、国际合作交流费',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'',
                //         sourcebudget:'',
                //         expenseaccount:'2、出版/文献/信息传播',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'',
                //         sourcebudget:'',
                //         expenseaccount:'3、知识产权事务',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'',
                //         sourcebudget:'',
                //         expenseaccount:'4、专家费',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'',
                //         sourcebudget:'',
                //         expenseaccount:'5、其他',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'',
                //         sourcebudget:'',
                //         expenseaccount:'十、新产品设计费',
                //         expensebudget:''
                //     },{
                //         sourceaccount:'',
                //         sourcebudget:'',
                //         expenseaccount:'十一、委托研发费用',
                //         expensebudget:''
                //     }
                // ],
                budgetList:[],
                monthList:[],
                appropriationPlan:[]
            }
        } 
    }
    created() {
        // 获取数据字典
        // this.getGenderList()
        // this.getProjectTypeList()
        // this.getProfessionalCategroyList()
    }

    activated() {
        if(Object.keys(this.$route.params).length > 0){
            if(this.$route.params.businessId){

               this.initData(this.$route.params.businessId)
           }
           if(this.$route.params.routerName){
               this.routerName = this.$route.params.routerName
           }else{
               this.routerName = 'setProjectApplyList'
           }
           Object.assign(this,this.$route.params.ids)
        }
    }
    // 初始化编辑数据
    initData(businessId) {
        this.yms = []
        this.processInstId = null
        this.serialNumber = null
        this.isSourceBudgetChange = false
        this.isExpenseBudgetChange = false
        const formRefs = ['doForm','infoForm','progressForm','partUnitForm','researchersForm','researchUserChangeForm','budgetForm','everyMonthForm','allocationForm']
        formRefs.forEach(item=>{
            this.$refs[item]?.resetFields();
        })
        this.$API.apiGetSetProjectApplyDetail({businessId}).then(res=>{
            this.baseInfo = Object.assign(this.baseInfo,this.getBaseInfo(),res.data);
            this.baseInfo.detailForm.progressPlan = this.baseInfo.progressPlan || []
            this.baseInfo.detailForm.attendUnit = this.baseInfo.attendUnit || []
            this.baseInfo.detailForm.researchUser = this.baseInfo.researchUser || []
            this.baseInfo.detailForm.researchUserChange = this.baseInfo.researchUserChange || []
            this.baseInfo.detailForm.budgetList = this.baseInfo.budgetList || []
            this.baseInfo.detailForm.monthList = this.baseInfo.monthList || []
            this.baseInfo.detailForm.appropriationPlan = this.baseInfo.appropriationPlan || []
            if(this.baseInfo.monthList[0]) {
                this.initTableHeader(this.baseInfo.monthList[0])
            }
            this.isSourceBudgetChange = this.baseInfo.detailForm.budgetList.some(item=>item.sourceBudgetChange || item.sourceBudgetChange=='0')
            this.isExpenseBudgetChange = this.baseInfo.detailForm.budgetList.some(item=>item.expenseBudgetChange || item.expenseBudgetChange=='0')
        })
    }
    initTableHeader(obj) {
        var ym = Object.keys(obj).filter(item=>item.includes('month'))
        ym.forEach(item=>{
            const years = item.substring(5,9)
            const months = item.substring(9,item.length)
            if(!this.yms.some(v=>v.years==years)){
                this.yms.push({
                    years,
                    months:[months]
                })
            }else {
                this.yms.filter(v=>v.years==years)[0].months.push(months)
            }
            
        })
        // 排序
        this.yms.sort((a,b)=>{
            var val1 = a.years
            var val2 = b.years
            return val1 - val2
        })
        this.yms.forEach(item=>{
            item.months.sort((a,b)=>a-b)
        })
        console.log(this.yms)
    }
    // 清空数据
    resetData(isRefresh) {
        // 返回清空当前页面数据
        // this.initData()
        this.$store.commit('DELETE_TAB', this.$route.path);
        this.$router.push({ name: this.routerName,params:{refresh:isRefresh}})
    }
    // 返回按钮
    backBtn(){
        this.resetData()
    }
    resolve(loadingBtn){
        // 审批意见校验通过
        this.$refs.approval.isCheckComplete().then((remark)=>{
            this.loadingBtn = loadingBtn;
            var params = {
                creatorOrgId : this.$store.getters.currentOrganization.organizationId,
                creatorOrgName : this.$store.getters.currentOrganization.organizationName,
                menuCode : this.MENU_CODE_LIST.setProjectApplyList,
                approveComment:remark,
                approveType:1,//1:同意 2:回退上一个节点 3：回退到发起人
                waitId:this.waitId
            }
            this.$API.apiApprovalSetProjectApply(params).then(res=>{
                this.$message({
                    type:'success',
                    message:'审批成功'
                })
                this.backBtn();
                this.loadingBtn = 0;
            }).catch(err=>{
                this.loadingBtn = 0;
            })
        })
    }
    // 退回发起人
    rejectOrigin(loadingBtn){
        // 审批意见校验通过
        this.$refs.approval.isCheckComplete().then((remark)=>{
            this.loadingBtn = loadingBtn;
            var params = {
                creatorOrgId : this.$store.getters.currentOrganization.organizationId,
                creatorOrgName : this.$store.getters.currentOrganization.organizationName,
                menuCode : this.MENU_CODE_LIST.setProjectApplyList,
                approveComment:remark,
                approveType:3,//1:同意 2:回退上一个节点 3：回退到发起人
                waitId:this.waitId
            }
            this.$API.apiApprovalSetProjectApply(params).then(res=>{
                this.$message({
                    type:'success',
                    message:'审批成功'
                })
                this.backBtn();
                this.loadingBtn = 0;
            }).catch(err=>{
                this.loadingBtn = 0;
            })
        })
    }
    // 退回上节点
    rejectOrigin(loadingBtn){
        // 审批意见校验通过
        this.$refs.approval.isCheckComplete().then((remark)=>{
            this.loadingBtn = loadingBtn;
            var params = {
                creatorOrgId : this.$store.getters.currentOrganization.organizationId,
                creatorOrgName : this.$store.getters.currentOrganization.organizationName,
                menuCode : this.MENU_CODE_LIST.setProjectApplyList,
                approveComment:remark,
                approveType:2,//1:同意 2:回退上一个节点 3：回退到发起人
                waitId:this.waitId
            }
            this.$API.apiApprovalSetProjectApply(params).then(res=>{
                this.$message({
                    type:'success',
                    message:'审批成功'
                })
                this.backBtn();
                this.loadingBtn = 0;
            }).catch(err=>{
                this.loadingBtn = 0;
            })
        })
    }
}
</script>

<style lang="scss" scoped>
.card-global{
    height: 100%;
    background-color: #fff;
    margin-bottom: 15px;
    .nav{
        position: relative;
        height: 40px;
        border-bottom: 1px #ddd solid;
        padding: 0 15px;
        font-size: 13px;
        box-sizing: border-box;
        line-height: 40px;
        font-weight: bold;
        i{
            margin-right: 5px;
            font-size: 13px;
        }
        .nav_right {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            .dropdownBtn{
                cursor: pointer;
                margin-left: 20px;
            }
        }
    }
}
.el-form {
    /deep/ .el-form-item.large {
        width: 66.6%;
        &.inline {
            width: 33.3%;
        }
        .el-form-item__label {
            width: 100% !important;
            text-align: left;
            display: block;
        }
        .el-form-item__content {
            margin-left: 80px;
        }
        .el-textarea__inner {
            &::placeholder {
                text-align: center;
                line-height: 90px;
            }
        }
    }
    .yearBudget {
        width: 100%;
        /deep/.span__inner {
            padding-left: 5px;
            padding-right: 5px;
        }
    }
    /deep/.redClass {
        color: #ca3d3d;
    }
    /deep/.greeClass {
        color: #aaa;
    }
}
</style>
