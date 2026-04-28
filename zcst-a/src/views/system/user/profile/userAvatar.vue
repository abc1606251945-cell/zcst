<template>
  <div class="user-info-head" @click="editCropper()">
    <img :src="options.img" title="点击上传头像" class="img-circle img-lg" />
    <el-dialog :title="title" v-model="open" width="860px" append-to-body @opened="modalOpened" @close="closeDialog">
      <div class="avatar-dialog-subtitle">建议上传清晰正面照片，支持 JPG/PNG，裁剪后将自动覆盖原头像。</div>
      <el-row class="avatar-cropper-row">
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <vue-cropper
            ref="cropper"
            :img="options.img"
            :info="true"
            :autoCrop="options.autoCrop"
            :autoCropWidth="options.autoCropWidth"
            :autoCropHeight="options.autoCropHeight"
            :fixedBox="options.fixedBox"
            :outputType="options.outputType"
            @realTime="realTime"
            v-if="visible"
          />
        </el-col>
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <div class="avatar-upload-preview">
            <img :src="options.previews.url" :style="options.previews.img" />
          </div>
        </el-col>
      </el-row>
      <div class="avatar-toolbar">
        <div class="avatar-toolbar-left">
          <el-upload
            action="#"
            :http-request="requestUpload"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <el-button>
              选择图片
              <el-icon class="el-icon--right"><Upload /></el-icon>
            </el-button>
          </el-upload>
          <el-button icon="Plus" @click="changeScale(1)" />
          <el-button icon="Minus" @click="changeScale(-1)" />
          <el-button icon="RefreshLeft" @click="rotateLeft()" />
          <el-button icon="RefreshRight" @click="rotateRight()" />
        </div>
        <div class="avatar-toolbar-right">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" :loading="uploading" @click="uploadImg()">{{ uploading ? "提交中..." : "保存头像" }}</el-button>
        </div>
      </div>
      <div class="avatar-tip">提示：点击左侧头像可再次打开本窗口修改，上传后会同步更新右上角头像。</div>
    </el-dialog>
  </div>
</template>

<script setup>
import "vue-cropper/dist/index.css"
import { VueCropper } from "vue-cropper"
import { uploadAvatar } from "@/api/system/user"
import useUserStore from "@/store/modules/user"

const userStore = useUserStore()
const { proxy } = getCurrentInstance()

const open = ref(false)
const visible = ref(false)
const title = ref("修改头像")
const uploading = ref(false)

//图片裁剪数据
const options = reactive({
  img: userStore.avatar,     // 裁剪图片的地址
  autoCrop: true,            // 是否默认生成截图框
  autoCropWidth: 200,        // 默认生成截图框宽度
  autoCropHeight: 200,       // 默认生成截图框高度
  fixedBox: true,            // 固定截图框大小 不允许改变
  outputType: "png",         // 默认生成截图为PNG格式
  filename: 'avatar',        // 文件名称
  previews: {}               //预览数据
})

/** 编辑头像 */
function editCropper() {
  open.value = true
}

/** 打开弹出层结束时的回调 */
function modalOpened() {
  visible.value = true
}

/** 覆盖默认上传行为 */
function requestUpload() {}

/** 向左旋转 */
function rotateLeft() {
  proxy.$refs.cropper.rotateLeft()
}

/** 向右旋转 */
function rotateRight() {
  proxy.$refs.cropper.rotateRight()
}

/** 图片缩放 */
function changeScale(num) {
  num = num || 1
  proxy.$refs.cropper.changeScale(num)
}

/** 上传预处理 */
function beforeUpload(file) {
  if (file.type.indexOf("image/") == -1) {
    proxy.$modal.msgError("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。")
    return false
  }
  const maxSize = 5
  const fileSizeMb = file.size / 1024 / 1024
  if (fileSizeMb > maxSize) {
    proxy.$modal.msgError(`上传头像图片大小不能超过 ${maxSize}MB`)
    return false
  } else {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => {
      options.img = reader.result
      options.filename = file.name
    }
  }
  return false
}

/** 上传图片 */
function uploadImg() {
  if (uploading.value) {
    return
  }
  proxy.$refs.cropper.getCropBlob(data => {
    if (!data) {
      proxy.$modal.msgError("请先选择图片")
      return
    }
    uploading.value = true
    let formData = new FormData()
    formData.append("avatarfile", data, options.filename)
    uploadAvatar(formData).then(response => {
      open.value = false
      // 修复：检查response.imgUrl是否为有效的路径，避免undefined导致的错误
      if (response.imgUrl && response.imgUrl !== 'undefined') {
        options.img = import.meta.env.VITE_APP_BASE_API + response.imgUrl
        userStore.avatar = options.img
      } else {
        // 使用默认头像或保持原头像
        options.img = userStore.avatar
      }
      proxy.$modal.msgSuccess("修改成功")
      visible.value = false
    }).finally(() => {
      uploading.value = false
    })
  })
}

/** 实时预览 */
function realTime(data) {
  options.previews = data
}

/** 关闭窗口 */
function closeDialog() {
  options.img = userStore.avatar
  visible.value = false
  open.value = false
}
</script>

<style lang='scss' scoped>
.user-info-head {
  position: relative;
  display: inline-block;
  height: 120px;
}

.user-info-head:hover:after {
  content: "+";
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  color: #eee;
  background: rgba(0, 0, 0, 0.5);
  font-size: 24px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  cursor: pointer;
  line-height: 110px;
  border-radius: 50%;
}

.avatar-dialog-subtitle {
  margin-bottom: 14px;
  color: #606266;
  font-size: 13px;
}

.avatar-cropper-row {
  margin-bottom: 14px;
}

.avatar-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.avatar-toolbar-left,
.avatar-toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}
</style>
