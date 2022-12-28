<template>

  <v-layout>
    <v-card>
      <v-navigation-drawer permanent>
        <v-list>
          <v-list-item v-for="item in items" :key="item.title" link>
            <v-list-item-icon>
              <v-icon>{{ item.icon }}</v-icon>
            </v-list-item-icon>

            <v-list-item-content>
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>

      </v-navigation-drawer>
    </v-card>

  </v-layout>








</template>

<script>

export default {
  data: () => ({
    selectedItem: null,
    items: [
      { title: '账号设置', icon: 'mdi-account-box' },
      { title: '显示设置', icon: 'mdi-draw' },
    ],
  }),



}
</script>