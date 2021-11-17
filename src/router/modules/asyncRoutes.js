export const apiRouteList = [
        {
            path: '/',
            component: 'Layout',
            redirect: '/overview',
            icon: 'icon-shouye1',
            noDropdown: true,
            meta:{role: ['admin','projectAdmin']},
            children: [{ path: 'overview', component: 'overview/overview', name: 'overview',meta:{title: '首页'}}]
        },
        {
            path: '/systemSetting',
            component: 'Layout',
            name: 'systemSetting',
            meta:{title: '系统管理'},
            icon: 'icon-jisuanjiyingjianComputerHardware103',
            children: [
                { path: 'userList', component: 'systemSetting/user/userList', name: 'userList',meta:{title: '员工管理',btnPermissions:[{menuButtonCode:'create'},{menuButtonCode:'edit'},{menuButtonCode:'delete'}] }},
                { path: 'userNew', component: 'systemSetting/user/table/create', name: 'userNew',meta:{title: '新建员工' ,keepAlive: true}, hidden: true},
                { path: 'userEdit', component: 'systemSetting/user/table/edit', name: 'userEdit',meta:{title: '编辑员工' ,keepAlive: true}, hidden: true},
                { path: 'organizationList', component: 'systemSetting/organization/organizationList', name: 'organizationList',meta:{title: '组织管理',btnPermissions:[] }},
                { path: 'projectList', component: 'systemSetting/project/projectList', name: 'projectList',meta:{title: '项目管理',btnPermissions:[{menuButtonCode:'create'},{menuButtonCode:'edit'},{menuButtonCode:'delete'}] }},
                { path: 'authorizationList', component: 'systemSetting/authorization/authorizationList', name: 'authorizationList',meta:{title: '权限管理',btnPermissions:[] }},
                { path: 'dictionaryList', component: 'systemSetting/dictionary/dictionaryList', name: 'dictionaryList',meta:{title: '数据字典',btnPermissions:[] }},
                { path: 'routerMenuList', component: 'systemSetting/routerMenu/routerMenuList', name: 'routerMenuList',meta:{title: '菜单管理',btnPermissions:[] }},
                { path: 'onlineManualsList', component: 'systemSetting/onlineManuals/onlineManualsList', name: 'onlineManualsList',meta:{title: '模板管理',btnPermissions:[{menuButtonCode:'edit'}] }},
                { path: 'operationLogList', component: 'systemSetting/operationLog/operationLogList', name: 'operationLogList',meta:{title: '操作日志',btnPermissions:[] }},
            ]
        },
    ]