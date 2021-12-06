export const constantRoutes = [
    {
        path: '/login',
        component: () =>
            import(/* webpackChunkName: "login" */ '@/views/login/login.vue'),
        hidden: true,
    },
    {
        path: '/404',
        component: () =>
            import(
                /* webpackChunkName: "errorPage" */ '@/views/errorPage/errorPage.vue'
                ),
        hidden: true,
    },
    {
        path: '/updatePassword',
        component: () =>
            import(
                /* webpackChunkName: "updatePassword" */ '@/components/updatePassword.vue'
                ),
        hidden: true,
    },
    {
        path: '/resetPassword',
        component: () =>
            import(
                /* webpackChunkName: "resetPassword" */ '@/components/resetPassword.vue'
                ),
        hidden: true,
    },
]
