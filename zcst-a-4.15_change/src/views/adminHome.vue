<template>
  <div class="app-container admin-home">
    <el-card>
      <div class="image-wrap">
        <img :src="devImg" alt="功能开发中" />
      </div>
    </el-card>
  </div>
</template>

<script setup name="AdminHome">
import devImg from "@/assets/images/develop.webp"
</script>

<style scoped lang="scss">
.admin-home {
  width: 100%;
}

.image-wrap {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px 0;
  min-height: 60vh;
}

.image-wrap img {
  max-width: 640px;
  width: 100%;
  height: auto;
  display: block;
}
</style>
