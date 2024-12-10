package self.adragon.aviaroute.data.repo

import self.adragon.aviaroute.data.database.dao.SegmentsDAO
import self.adragon.aviaroute.data.model.Segment

class SegmentRepository(private val segmentsDAO: SegmentsDAO) {
    fun getByIndex(index: Int) = segmentsDAO.segByIndex(index)

    private fun sumSegmentsFLightTime(indexes: IntArray) =
        indexes.sumOf { segmentsDAO.getSegmentFlightTime(it) }

    fun currentSegmentDepartureTime(segmentIndexes: IntArray, departureTimeEpoch: Long): Long {
        val curSeg = getByIndex(segmentIndexes.last())
        val curFlightTime = curSeg.flightTimeEpochSeconds
        val allSegmentsFlightTime = sumSegmentsFLightTime(segmentIndexes)
        val otherSegmentsFlightTime = allSegmentsFlightTime - curFlightTime

        return departureTimeEpoch + otherSegmentsFlightTime
    }

    suspend fun insert(segment: Segment) = segmentsDAO.insert(segment)
    suspend fun insertAll(segments: List<Segment>) = segmentsDAO.insertAll(segments)
}