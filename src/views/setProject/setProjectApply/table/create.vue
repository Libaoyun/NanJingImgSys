<template>
    <div style="padding-bottom: 46px">
        <!-- 主信息 -->
        <card-global cardTitle="主信息">
            <el-form ref="doForm" :inline="true" :rules="formRules" :model="baseInfo" size="mini" label-position="right" label-width="80px">
                <el-form-item label="申请单号:" prop="serialNumber">
                    <el-input v-model="baseInfo.serialNumber" placeholder="自动生成RDPI" disabled></el-input>
                </el-form-item>
                <el-form-item label="编制人:" prop="creatorUserName">
                    <el-input  v-model="baseInfo.creatorUserName" disabled></el-input>
                </el-form-item>
                <el-form-item label="编制日期:" prop="createdDate">
                    <el-input  :value="baseInfo.createdDate" disabled></el-input>
                </el-form-item>
                <el-form-item label="项目名称:" prop="projectName">
                    <el-input v-model="baseInfo.projectName" placeholder="请输入项目名称"></el-input>
                </el-form-item>
                <el-form-item label="单位名称:" prop="unitName">
                    <el-input v-model="baseInfo.unitName" placeholder="请输入项目部公章全称"></el-input>
                </el-form-item>
                <el-form-item label="单位地址:" prop="unitAddress">
                    <el-input v-model="baseInfo.unitAddress" placeholder="请输入项目部所在地址"></el-input>
                </el-form-item>
                <el-form-item label="申请人:" prop="applyUserName" @click.native="chooseUser">
                    <el-input readonly v-model="baseInfo.applyUserName" placeholder="点击弹出用户列表选择申请人"></el-input>
                </el-form-item>
                <el-form-item label="性别:" prop="genderCode">
                    <el-select v-model="baseInfo.genderCode" placeholder="请选择性别" @change="baseInfo.gender = GET_DICTIONARY_TEXT(genderList,baseInfo.genderCode)">
                        <el-option v-for="item in genderList" :label="item.label" :value="item.value" :key='item.value'></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="年龄:" prop="age">
                    <el-input v-model.number="baseInfo.age" placeholder="请输入年龄"></el-input>
                </el-form-item>
                <el-form-item label="职务:" prop="postName">
                    <el-select v-if="departmentList.length > 1" v-model="baseInfo.postCode">
                        <el-option v-for="item in departmentList" :key="item.postCode" :value="item.postCode" :label="item.postName"></el-option>
                    </el-select>
                    <el-input v-else v-model="baseInfo.postName" disabled placeholder="请先选择申请人"></el-input>
                    
                </el-form-item>
                <el-form-item label="电话号码:" prop="telephone">
                    <el-input v-model="baseInfo.telephone" placeholder="请输入电话号码"></el-input>
                </el-form-item>
                <el-form-item label="申请经费:" prop="applyAmount">
                    <el-input v-model="baseInfo.applyAmount" placeholder="请输入申请经费">
                        <template slot="append">（万元）</template>
                    </el-input>
                </el-form-item>
                <el-form-item label="起始年度:" prop="startYear">
                    <el-date-picker v-model="baseInfo.startYear" value-format="yyyy-MM-dd" type="date" placeholder="请选择起始年度的1月1日为起始年度"></el-date-picker>
                </el-form-item>
                <el-form-item label="结束年度:" prop="endYear">
                    <el-date-picker v-model="baseInfo.endYear" value-format="yyyy-MM-dd" type="date" placeholder="请选择结束年度的12月31日为结束年度"></el-date-picker>
                </el-form-item>
                <el-form-item label="邮编:" prop="zipCode">
                    <el-input v-model="baseInfo.zipCode" placeholder="请输入邮编"></el-input>
                </el-form-item>
                <el-form-item label="专业类别:" prop="professionalCategoryCode">
                    <el-select v-model="baseInfo.professionalCategoryCode" placeholder="请选择专业类别" @change="baseInfo.professionalCategory = GET_DICTIONARY_TEXT(professionalCategroyList,baseInfo.professionalCategoryCode)">
                        <el-option v-for="item in professionalCategroyList" :label="item.label" :value="item.value" :key='item.value'></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="项目类型:" prop="projectTypeCode">
                    <el-select v-model="baseInfo.projectTypeCode" placeholder="请选择项目类型" @change="baseInfo.projectType = GET_DICTIONARY_TEXT(projectTypeList,baseInfo.projectTypeCode)">
                        <el-option v-for="item in projectTypeList" :label="item.label" :value="item.value" :key='item.value'></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否鉴定:" prop="identify">
                    <el-select v-model="baseInfo.identify" placeholder="请选择是否进行成果鉴定">
                        <el-option label="是" value="1"></el-option>
                        <el-option label="否" value="0"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="研究内容题要:" prop="researchContents" class="large">
                    <el-input type="textarea" v-model="baseInfo.researchContents" minlength="200" rows="5" resize="none" placeholder="内容不得少于200字"></el-input>
                </el-form-item>
                <el-form-item label="申报单位审查意见:" prop="reviewComments" class="large">
                    <el-input type="textarea" v-model="baseInfo.reviewComments" minlength="200" rows="5" resize="none" placeholder="内容不得少于200字"></el-input>
                </el-form-item>
                <el-form-item label="申报单位盖章:" prop="val177" class="large inline">
                    <el-input type="textarea" v-model="baseInfo.val177" minlength="200" rows="5" resize="none" placeholder="盖章处" readonly></el-input>
                </el-form-item>
            </el-form>
        </card-global>
        <!-- 立项调研信息 -->
        <card-global cardTitle="立项调研信息">
            <template v-slot:button>
                <upload-template importUrl="apiUploadSurvey" @importBtn="importTemplate($event,1)"></upload-template>
            </template>
            <el-form ref="infoForm" :inline="true" :rules="formRules" :model="baseInfo" size="mini" label-position="right" label-width="80px">
                <el-form-item label="一、国内外现状:" prop="currentSituation" class="large">
                    <el-input type="textarea" v-model="baseInfo.currentSituation" minlength="200" rows="5" resize="none" placeholder="内容不得少于200字"></el-input>
                </el-form-item>
                <el-form-item label="二、研发目的和意义:" prop="purposeSignificance" class="large">
                    <el-input type="textarea" v-model="baseInfo.purposeSignificance" minlength="200" rows="5" resize="none" placeholder="内容不得少于200字"></el-input>
                </el-form-item>
                <el-form-item label="三、主要研究内容及研究方法:" prop="contentMethod" class="large">
                    <el-input type="textarea" v-model="baseInfo.contentMethod" minlength="200" rows="5" resize="none" placeholder="内容不得少于200字"></el-input>
                </el-form-item>
                <el-form-item label="四、要达到的目标、成果形式及主要技术指标:" prop="targetResults" class="large">
                    <el-input type="textarea" v-model="baseInfo.targetResults" minlength="200" rows="5" resize="none" placeholder="内容不得少于200字"></el-input>
                </el-form-item>
                <el-form-item label="五、现有研发条件和工作基础:" prop="basicConditions" class="large">
                    <el-input type="textarea" v-model="baseInfo.basicConditions" minlength="200" rows="5" resize="none" placeholder="内容不得少于200字"></el-input>
                </el-form-item>
                <el-form-item label="六、研发项目创新点:" prop="innovationPoints" class="large">
                    <el-input type="textarea" v-model="baseInfo.innovationPoints" minlength="200" rows="5" resize="none" placeholder="内容不得少于200字"></el-input>
                </el-form-item>
                <el-form-item label="七、成果转化的可行性分析:" prop="feasibilityAnalysis" class="large">
                    <el-input type="textarea" v-model="baseInfo.feasibilityAnalysis" minlength="200" rows="5" resize="none" placeholder="内容不得少于200字"></el-input>
                </el-form-item>
            </el-form>
        </card-global>
        <!-- 研发项目立项申请-进度计划 -->
        <card-global cardTitle="研发项目立项申请-进度计划" type='table'>
            <template v-slot:button>
                <upload-template importUrl="apiUploadProgress" @importBtn="importTemplate($event,2)"></upload-template>
            </template>
            <el-form ref="progressForm" :inline="true" :rules="progressRules" :model="baseInfo.detailForm" size="mini" :show-message="false">
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
                            <el-form-item :prop="'progressPlan.'+scope.$index+'.years'" :rules="progressRules['years']">
                                <el-date-picker v-model="scope.row.years" type="year" format="yyyy"></el-date-picker>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="planTarget" label="计划及目标" width="300" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'progressPlan.'+scope.$index+'.planTarget'" :rules="progressRules['planTarget']">
                                <el-input v-model="scope.row.planTarget"></el-input>
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
                    <el-table-column label="操作" width="60" align="center">
                        <template slot-scope="scope">
                            <el-popconfirm title="是否要删除此行？" @confirm="deleteTableList(scope,'progressPlan')" placement="top" cancelButtonType="plain">
                                <i slot="reference" class="el-icon-delete delete-detail-btn"></i>
                            </el-popconfirm>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
            <el-button size="mini" icon="el-icon-plus" class="table-add-btn" @click="addTableList('progressForm','progressPlan')">新增</el-button>
        </card-global>
        <!-- 研发项目立项申请-参加单位 -->
        <card-global cardTitle="研发项目立项申请-参加单位" type="table">
            <template v-slot:button>
                <upload-template importUrl="apiUploadUnit" @importBtn="importTemplate($event,3)"></upload-template>
            </template>
            <el-form ref="partUnitForm" :inline="true" :rules="partUnitRules" :model="baseInfo.detailForm" size="mini" :show-message="false">
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
                            <el-form-item :prop="'attendUnit.'+scope.$index+'.unitName'" :rules="partUnitRules['unitName']">
                                <el-input v-model="scope.row.unitName"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="taskDivision" label="研究任务及分工" width="300" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'attendUnit.'+scope.$index+'.taskDivision'" :rules="partUnitRules['taskDivision']">
                                <el-input v-model="scope.row.taskDivision"></el-input>
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
                    <el-table-column label="操作" width="60" align="center">
                        <template slot-scope="scope">
                            <el-popconfirm title="是否要删除此行？" @confirm="deleteTableList(scope,'attendUnit')" placement="top" cancelButtonType="plain">
                                <i slot="reference" class="el-icon-delete delete-detail-btn"></i>
                            </el-popconfirm>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
            <el-button size="mini" icon="el-icon-plus" class="table-add-btn"  @click="addTableList('partUnitForm','attendUnit')">新增</el-button>
        </card-global>
        <!-- 研发项目立项申请-研究人员（初始） -->
        <card-global cardTitle="研发项目立项申请-研究人员（初始）" type="table">
            <template v-slot:button>
                <upload-template importUrl="apiUploadUser" @importBtn="importTemplate($event,4)"></upload-template>
            </template>
            <el-form ref="researchersForm" :inline="true" :rules="researchersRules" :model="baseInfo.detailForm" size="mini" :show-message="false">
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
                            <el-form-item :prop="'researchUser.'+scope.$index+'.userName'" :rules="researchersRules['userName']">
                                <el-input v-model="scope.row.userName"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="idCard" label="身份证号码" width="160" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.idCard'" :rules="researchersRules['idCard']">
                                <el-input v-model="scope.row.idCard"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="age" label="年龄" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.age'" :rules="researchersRules['age']">
                                <el-input v-model="scope.row.age"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="gender" label="性别" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.gender'" :rules="researchersRules['gender']">
                                <el-input v-model="scope.row.gender"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="education" label="学历" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.education'" :rules="researchersRules['education']">
                                <el-input v-model="scope.row.education"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="belongDepartment" label="所属部门" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.belongDepartment'" :rules="researchersRules['belongDepartment']">
                                <el-input v-model="scope.row.belongDepartment"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="belongPost" label="职务职称" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.belongPost'" :rules="researchersRules['belongPost']">
                                <el-input v-model="scope.row.belongPost"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="majorStudied" label="所学专业" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.majorStudied'" :rules="researchersRules['majorStudied']">
                                <el-input v-model="scope.row.majorStudied"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="majorWorked" label="现从事专业" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.majorWorked'" :rules="researchersRules['majorWorked']">
                                <el-input v-model="scope.row.majorWorked"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="belongUnit" label="所在单位" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.belongUnit'" :rules="researchersRules['belongUnit']">
                                <el-input v-model="scope.row.belongUnit"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="taskDivision" label="研究任务及分工" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.taskDivision'" :rules="researchersRules['taskDivision']">
                                <el-input v-model="scope.row.taskDivision"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="workRate" label="全时率" width="100" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.workRate'" :rules="researchersRules['workRate']">
                                <el-input v-model="scope.row.workRate"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="telephone" label="联系电话" width="140" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.telephone'" :rules="researchersRules['telephone']">
                                <el-input v-model="scope.row.telephone"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="startDate" label="参与研发开始日期" width="160" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.startDate'" :rules="researchersRules['startDate']">
                                <el-date-picker v-model="scope.row.startDate" value-format="yyyy-MM-dd" type="date"></el-date-picker>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="endDate" label="参与研发结束日期" width="160" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'researchUser.'+scope.$index+'.endDate'" :rules="researchersRules['endDate']">
                                <el-date-picker v-model="scope.row.endDate" value-format="yyyy-MM-dd" type="date"></el-date-picker>
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
                    <el-table-column fixed="right" label="操作" width="60" align="center">
                        <template slot-scope="scope">
                            <el-popconfirm title="是否要删除此行？" @confirm="deleteTableList(scope,'researchUser')" placement="top" cancelButtonType="plain">
                                <i slot="reference" class="el-icon-delete delete-detail-btn"></i>
                            </el-popconfirm>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
            <el-button size="mini" icon="el-icon-plus" class="table-add-btn" @click="addTableList('researchersForm','researchUser')">新增</el-button>
        </card-global>
        <!-- 研发项目立项申请-经费预算 -->
        <card-global cardTitle="研发项目立项申请-经费预算" type="table">
            <template v-slot:button>
                <upload-template importUrl="apiUploadBudget" @importBtn="importTemplate($event,5)"></upload-template>
            </template>
            <el-form ref="budgetForm" :inline="true" :rules="budgetRules" :model="baseInfo.detailForm" size="mini" :show-message="false">
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
                        <el-table-column prop="sourceBudget" label="预算数 (万元)" align="center">
                            <template slot-scope="scope">
                                <el-form-item v-if="scope.$index<=6" :prop="'budgetList.'+scope.$index+'.sourceBudget'" :rules="budgetRules['sourceBudget']">
                                    <el-input-number :controls="false" :precision="2" v-model="scope.row.sourceBudget"></el-input-number>
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
                        <el-table-column prop="expenseBudget" label="预算数 (万元)" align="center">
                            <template slot-scope="scope">
                                <el-form-item :prop="'budgetList.'+scope.$index+'.expenseBudget'" :rules="budgetRules['expenseBudget']">
                                    <el-input-number :controls="false" :precision="2" v-model="scope.row.expenseBudget"></el-input-number>
                                </el-form-item>
                            </template>
                        </el-table-column>
                    </el-table-column>
                </el-table>
            </el-form>
        </card-global>
        <!-- 研发项目立项申请-经费预算（每月预算） -->
        <card-global cardTitle="研发项目立项申请-经费预算（每月预算）" type="table">
            <template v-slot:button>
                <upload-template importUrl="apiUploadMonth" @importBtn="importTemplate($event,6)"></upload-template>
            </template>
            <el-form ref="everyMonthForm" :inline="true" :rules="everyMonthRules" :model="baseInfo.detailForm" size="mini" :show-message="false">
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
                                <el-form-item v-if="scope.$index<=6" :prop="'monthList.'+scope.$index+'.sourcebudget'" :rules="everyMonthRules['sourcebudget']">
                                    <el-input-number :controls="false" :precision="2" v-model="scope.row.sourcebudget"></el-input-number>
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
                                <el-form-item :prop="'monthList.'+scope.$index+'.expensebudget'" :rules="everyMonthRules['expensebudget']">
                                    <el-input-number :controls="false" :precision="2" v-model="scope.row.expensebudget"></el-input-number>
                                </el-form-item>
                            </template>
                        </el-table-column>
                    </el-table-column>
                    <el-table-column label="年度预算 (按月填报)" align="center">
                        <el-table-column v-for="item in yms" :key="item.years" :label="item.years+'年'" align="center">
                            <el-table-column v-for="c in item.months" :key="'month'+item.years+c" :prop="c" :label="c+'月'" align="center" width="80">
                                <template slot-scope="scope">
                                    <el-form-item v-if="scope.$index!=0" :prop="'monthList.'+scope.$index+'.month'+item.years+c" :rules="everyMonthRules['month'+item.years+c]">
                                        <el-input-number class="yearBudget" :controls="false" :precision="2" v-model="scope.row['month'+item.years+c]"></el-input-number>
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
            <template v-slot:button>
                <upload-template importUrl="apiUploadAppropriation" @importBtn="importTemplate($event,7)"></upload-template>
            </template>
            <el-form ref="allocationForm" :inline="true" :rules="allocationRules" :model="baseInfo.detailForm" size="mini" :show-message="false">
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
                            <el-form-item :prop="'appropriationPlan.'+scope.$index+'.years'" :rules="allocationRules['years']">
                                <el-input v-model="scope.row.years"></el-input>
                            </el-form-item>
                    </template>
                    </el-table-column>
                    <el-table-column prop="planAmount" label="计划 (万元)" width="300" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'appropriationPlan.'+scope.$index+'.planAmount'" :rules="allocationRules['planAmount']">
                                <el-input v-model="scope.row.planAmount"></el-input>
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
                    <el-table-column label="操作" width="60" align="center">
                        <template slot-scope="scope">
                            <el-popconfirm title="是否要删除此行？" @confirm="deleteTableList(scope,'appropriationPlan')" placement="top" cancelButtonType="plain">
                                <i slot="reference" class="el-icon-delete delete-detail-btn"></i>
                            </el-popconfirm>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
            <el-button size="mini" icon="el-icon-plus" class="table-add-btn" @click="addTableList('allocationForm','appropriationPlan')">新增</el-button>
        </card-global>
        <div class="global-fixBottom-actionBtn">
            <el-button size="mini" @click="backBtn">返回</el-button>
            <loading-btn size="mini" @click="saveBtn(2)" :loading="loadingBtn">保存</loading-btn>
            <loading-btn size="mini" type="primary" @click="submitBtn(1)" :loading="loadingBtn">提交</loading-btn>
            <loading-btn size="mini" @click="previewBtn(3)" :loading="loadingBtn">预览合同</loading-btn>  
        </div>
    </div>
</template>

<script>
import { Component, Mixins, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import dictionaryMixin from '@/mixins/dictionaryMixin'
import { checkForm } from '@/utils/index'
import CardGlobal from '../../../../components/cardGlobal.vue';
import rule from './rule'
import $alert from '../alert'
import UploadTemplate from '../components/uploadTemplate.vue'
import { formatTimeDate } from '@/utils/index'

@Component({
    name: 'setProjectApplyNew',
    components: {
        CardGlobal,
        UploadTemplate
    }
})
   
export default class extends Mixins(tableMixin,dictionaryMixin,rule) {
    loadingBtn = 0;
    baseInfo = this.getBaseInfo()
    dialogImageUrl = ''
    dialogVisible = false
    disabled = false
    departmentList = []
    yms = []
    everyMonthRules = {
        sourcebudget: [
            { required: true, message: '', trigger: 'change' }
        ],
        expensebudget: [
            { required: true, message: '', trigger: 'change' }
        ],
    }
    // 设置空数据
    getBaseInfo(){
       return {
            creatorUserName:this.$store.getters.userInfo?.userName,
            creatorUserId:this.$store.getters.userInfo?.userCode,
            createdDate: formatTimeDate(new Date().getTime(),'yyyy-MM-dd'),
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
        this.getGenderList()
        this.getProjectTypeList()
        this.getProfessionalCategroyList()
    }

    activated() {
        if(Object.keys(this.$route.params).length > 0){
           if(this.$route.params.initData){
               this.initData()
           }
        }
    }

    // 初始化新建数据
    initData() {
        this.departmentList = []
        this.yms = []
        const formRefs = ['doForm','infoForm','progressForm','partUnitForm','researchersForm','budgetForm','everyMonthForm','allocationForm']
        formRefs.forEach(item=>{
            this.$refs[item].resetFields();
        })
        this.baseInfo = Object.assign(this.baseInfo,this.getBaseInfo())
    }
    // 选择申请人
    chooseUser() {
        $alert.alertChooseUser().then(res=>{
            console.log(res)
            this.baseInfo.applyUserName = res[0].userName
            this.baseInfo.applyUserId = res[0].userCode
            this.baseInfo.genderCode = res[0].genderCode
            this.baseInfo.gender = res[0].gender
            this.baseInfo.age = res[0].age
            this.baseInfo.telephone = res[0].mobilePhone
            this.departmentList = res[0].departmentList
            if(this.departmentList.length === 1) {
                this.baseInfo.postCode = this.departmentList[0].postCode
                this.baseInfo.postName = this.departmentList[0].postName
            }
        })
    }
    addTableList(formName,tableName) {
        this.$refs[formName].validate(valid => {
            if (valid) {
                let tableObj = {}
                switch(tableName) {
                    case 'progressPlan' :
                        tableObj = {
                            years:'',
                            planTarget:'',
                            creatorUser:this.$store.getters.userInfo?.userName,
                            creatorUserId:this.$store.getters.userInfo?.userCode,
                            createTime: formatTimeDate(new Date().getTime(),'yyyy-MM-dd HH:mm:ss'),
                        }
                    break;
                    case 'attendUnit' :
                        tableObj = {
                            unitName:'',
                            taskDivision:'',
                            creatorUser:this.$store.getters.userInfo?.userName,
                            creatorUserId:this.$store.getters.userInfo?.userCode,
                            createTime: formatTimeDate(new Date().getTime(),'yyyy-MM-dd HH:mm:ss'),
                        }
                    break;
                    case 'researchUser' :
                        tableObj = {
                            userName:'',
                            idCard:'',
                            age:'',
                            gender:'',
                            education:'',
                            belongDepartment:'',
                            belongPost:'',
                            majorStudied:'',
                            majorWorked:'',
                            belongUnit:'',
                            taskDivision:'',
                            workRate:'',
                            telephone:'',
                            startDate:'',
                            endDate:'',
                            creatorUser:this.$store.getters.userInfo?.userName,
                            creatorUserId:this.$store.getters.userInfo?.userCode,
                            createTime: formatTimeDate(new Date().getTime(),'yyyy-MM-dd HH:mm:ss'),
                        }
                    break;
                    case 'appropriationPlan' :
                        tableObj = {
                            years:'',
                            planAmount:'',
                            creatorUser:this.$store.getters.userInfo?.userName,
                            creatorUserId:this.$store.getters.userInfo?.userCode,
                            createTime: formatTimeDate(new Date().getTime(),'yyyy-MM-dd HH:mm:ss'),
                        }
                    break;
                }
                this.baseInfo.detailForm[tableName].push(tableObj)
            } else {
              return false
            }
        })
    }
    deleteTableList(scope,tableName) {
        // if(this.baseInfo.detailForm[tableName].length === 1) {
        //     this.$message('至少填写一条数据')
        //     return
        // }
        this.baseInfo.detailForm[tableName].splice(scope.$index, 1)
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
        // 校验
        this.yms.forEach(item=>{
            item.months.forEach(c=>{
                this.everyMonthRules['month'+item.years+c] = [
                    { required: true, message: '', trigger: 'change' }
                ]
            })
        })
        console.log(this.yms)
    }
    // 导入模板
    importTemplate(data,index) {
        if(index === 1) {
            // 立项调研信息
            const keysArr = ['currentSituation','purposeSignificance','contentMethod','targetResults','basicConditions','innovationPoints','feasibilityAnalysis']
            keysArr.forEach(item=>{
                this.baseInfo[item] = data[item]
            })
        }else if(index === 2) {
            // 研发项目立项申请-进度计划
            this.baseInfo.detailForm.progressPlan = data || []
        }else if(index === 3) {
            // 研发项目立项申请-参加单位
            this.baseInfo.detailForm.attendUnit = data || []
        }else if(index === 4) {
            // 研发项目立项申请-研究人员（初始）
            this.baseInfo.detailForm.researchUser = data || []
        }else if(index === 5) {
            // 研发项目立项申请-经费预算
            this.baseInfo.detailForm.budgetList = data || []
        }else if(index === 6) {
            // 研发项目立项申请-经费预算（每月预算）
            this.baseInfo.detailForm.monthList = data || []
            if(data[0]) {
                this.initTableHeader(data[0])
            }
        }else if(index === 7) {
            // 研发项目立项申请-拨款计划
            this.baseInfo.detailForm.appropriationPlan = data || []
        }
        
    }
    formatSendData(data) {
        data = JSON.parse(JSON.stringify(data));
        data.creatorOrgId = this.$store.getters.currentOrganization.organizationId
        data.creatorOrgName = this.$store.getters.currentOrganization.organizationName
        data.menuCode = this.MENU_CODE_LIST.setProjectApplyList

        data.progressPlan = data.detailForm.progressPlan
        data.attendUnit = data.detailForm.attendUnit
        data.researchUser = data.detailForm.researchUser
        data.budgetList = data.detailForm.budgetList
        data.monthList = data.detailForm.monthList
        data.appropriationPlan = data.detailForm.appropriationPlan
        delete data.detailForm
        return data
    }
    // 清空数据
    resetData(isRefresh) {
        // 返回清空当前页面数据
        // this.initData()
        this.$store.commit('DELETE_TAB', this.$route.path);
        this.$router.push({ name: 'setProjectApplyList',params:{refresh:isRefresh}})
    }
    // 保存按钮
    saveBtn(loadingBtnIndex){
        if(document.getElementsByClassName("is-error").length > 0){
           this.$message({
                type: 'warning',
                message: '请将信息填写正确!'
            });
            return 
        }
        let params = this.formatSendData(this.baseInfo);
        params.operationType = 1; //1-保存,2-提交
        this.loadingBtn = loadingBtnIndex;
        this.$API.apiCreateSetProjectApply(params).then(res => {
            this.loadingBtn = 0;
            this.$message({
            type: "success",
            message: "保存成功!"
            });
            this.resetData(true);
        })
        .catch(() => {
            this.loadingBtn = 0;
        });
    }
    // 提交按钮
    submitBtn(loadingBtnIndex) {
        const _self = this
        let formArr = ['doForm','infoForm','progressForm','partUnitForm','researchersForm','budgetForm','everyMonthForm','allocationForm']
        let resultArr = []
        formArr.forEach(item => { //根据表单的ref校验
            resultArr.push(checkForm(_self,item))
        })
        Promise.all(resultArr).then(() => {
            this.$confirm('确定保存当前表单?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
                }).then(() => {
                    let params = this.formatSendData(this.baseInfo);
                    params.operationType = 2; //1-保存,2-提交
                    this.loadingBtn = loadingBtnIndex;
                    this.$API.apiCreateSetProjectApply(params).then(res=>{
                        this.loadingBtn = 0;
                        this.$message({
                            type: 'success',
                            message: '提交成功!'
                        });
                        this.resetData(true)
                    }).catch(() => {
                        this.loadingBtn = 0;
                    })
            });
        }).catch(() => {
            this.$message({
            type: "warning",
            message: "请将信息填写完整！"
            });
            this.loadingBtn = 0;
        });
    }
    // 预览合同
    previewBtn(loadIndex) {
        let params = this.formatSendData(this.baseInfo);
        this.EXPORT_FILE([],'print',{url:'/rdexpense/projectApply/exportPdf',params},true);
    }
    // 返回按钮
    backBtn(){
        this.$confirm('未保存的数据将丢失，是否返回?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(()=>{
            this.resetData()
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
        /deep/.el-input__inner {
            padding-left: 5px;
            padding-right: 5px;
        }
    }
}
</style>
