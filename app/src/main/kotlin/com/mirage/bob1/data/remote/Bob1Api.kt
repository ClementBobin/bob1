package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path

/** Authenticated endpoints (`/api/v1`). List endpoints use the HTTP `QUERY` method with a JSON body. */
interface Bob1Api {
    // Me / family
    @GET("me") suspend fun me(): UserDto
    @GET("family") suspend fun family(): FamilyDto
    @POST("family/invitations") suspend fun createInvitation(): InvitationDto
    @POST("auth/logout") suspend fun logout(@Body body: RefreshRequestDto)

    // Club
    @HTTP(method = "QUERY", path = "divisions", hasBody = true) suspend fun divisions(@Body q: ClubQueryDto): PagedDto<DivisionDto>
    @HTTP(method = "QUERY", path = "venues", hasBody = true) suspend fun venues(@Body q: ClubQueryDto): PagedDto<VenueDto>

    // Matches
    @HTTP(method = "QUERY", path = "matches", hasBody = true) suspend fun matches(@Body q: MatchQueryDto): PagedDto<MatchDto>
    @GET("matches/{id}") suspend fun match(@Path("id") id: String): MatchDto

    // Participation
    @GET("matches/{id}/participation") suspend fun participation(@Path("id") matchId: String): ParticipationDto
    @POST("matches/{id}/presence/confirm") suspend fun confirmPresence(@Path("id") matchId: String)
    @POST("slots/{id}/join") suspend fun joinSlot(@Path("id") slotId: String)
    @DELETE("slots/{id}/join") suspend fun leaveSlot(@Path("id") slotId: String)
    @HTTP(method = "QUERY", path = "me/commitments", hasBody = true) suspend fun commitments(@Body q: EmptyQueryDto): List<CommitmentDto>

    // Carpool
    @HTTP(method = "QUERY", path = "matches/{id}/rides", hasBody = true) suspend fun rides(@Path("id") matchId: String, @Body q: EmptyQueryDto): List<RideDto>
    @POST("matches/{id}/rides") suspend fun offerRide(@Path("id") matchId: String, @Body body: OfferRideRequestDto)
    @DELETE("rides/{id}") suspend fun cancelRide(@Path("id") rideId: String)
    @POST("rides/{id}/booking") suspend fun book(@Path("id") rideId: String)
    @DELETE("rides/{id}/booking") suspend fun cancelBooking(@Path("id") rideId: String)

    // Ranking
    @HTTP(method = "QUERY", path = "rankings", hasBody = true) suspend fun personRanking(@Body q: RankingQueryDto): PersonRankingDto
    @HTTP(method = "QUERY", path = "rankings", hasBody = true) suspend fun familyRanking(@Body q: RankingQueryDto): FamilyRankingDto

    // Notifications + push
    @HTTP(method = "QUERY", path = "notifications", hasBody = true) suspend fun notifications(@Body q: NotificationQueryDto): List<NotificationDto>
    @POST("notifications/{id}/read") suspend fun markRead(@Path("id") id: String)
    @POST("notifications/read-all") suspend fun markAllRead()
    @POST("me/push-tokens") suspend fun registerPushToken(@Body body: PushTokenDto)
    @HTTP(method = "DELETE", path = "me/push-tokens", hasBody = true) suspend fun unregisterPushToken(@Body body: PushTokenDto)

    // Staff (role Staff)
    @HTTP(method = "QUERY", path = "staff/assignments", hasBody = true) suspend fun staffAssignments(@Body q: EmptyQueryDto): List<StaffAssignmentDto>
    @HTTP(method = "QUERY", path = "staff/open-slots", hasBody = true) suspend fun openStaffSlots(@Body q: OpenSlotsQueryDto): List<OpenStaffMatchDto>
    @HTTP(method = "QUERY", path = "rankings/me/entries", hasBody = true) suspend fun myEntries(@Body q: MyEntriesQueryDto): List<EntryDto>
}
