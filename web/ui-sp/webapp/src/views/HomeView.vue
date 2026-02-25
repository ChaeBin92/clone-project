<template>
  <!-- 로컬 구동 여부를 빠르게 확인하기 위한 최소 화면 -->
  <div class="home">
    <h1>Hello UI-SP</h1>
    <p>Local Development Mode Running</p>
  </div>
</template>

<script setup lang="ts">
// 현재는 라우팅/스타일/빌드 검증 단계라 비즈니스 로직을 넣지 않는다.
</script>

<style scoped>
/* 최소 화면에서 가독성을 확보하기 위한 기본 여백/글자 크기 */
.home {
  padding: 40px;
  font-size: 20px;
}
</style>
