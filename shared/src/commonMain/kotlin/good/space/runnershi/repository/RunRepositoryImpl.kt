package good.space.runnershi.repository

import good.space.runnershi.mapper.RunMapper
import good.space.runnershi.model.domain.RunResult
import good.space.runnershi.network.ApiClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class RunRepositoryImpl(
    private val apiClient: ApiClient
) : RunRepository {
    
    override suspend fun saveRun(runResult: RunResult): Result<String> {
        return try {
            // 1. Domain Model -> DTO 변환
            val request = RunMapper.mapToCreateRequest(runResult)
            
            // 2. API 호출 (인증은 ApiClient의 httpClient가 자동 처리)
            val response = apiClient.httpClient.post("${apiClient.baseUrl}/api/v1/running/run-records") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            if (response.status == HttpStatusCode.OK) {
                // 서버는 Long 타입의 runId를 반환
                val runId = response.body<Long>()
                Result.success(runId.toString())
            } else {
                Result.failure(Exception("러닝 기록 업로드 실패: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

