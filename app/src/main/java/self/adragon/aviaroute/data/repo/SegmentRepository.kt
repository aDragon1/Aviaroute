package self.adragon.aviaroute.data.repo

import self.adragon.aviaroute.data.database.dao.SegmentsDAO
import self.adragon.aviaroute.data.model.Segment

class SegmentRepository(private val segmentsDAO: SegmentsDAO) {
    fun getByIndex(index: Int) = segmentsDAO.segByIndex(index)

    suspend fun insert(segment: Segment) = segmentsDAO.insert(segment)
}