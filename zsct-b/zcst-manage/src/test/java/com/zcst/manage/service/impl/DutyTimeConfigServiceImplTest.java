package com.zcst.manage.service.impl;

import com.zcst.manage.domain.DutyTimeConfig;
import com.zcst.manage.mapper.DutyScheduleMapper;
import com.zcst.manage.mapper.DutyTimeConfigMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DutyTimeConfigServiceImplTest
{
    @Mock
    private DutyTimeConfigMapper dutyTimeConfigMapper;

    @Mock
    private DutyScheduleMapper dutyScheduleMapper;

    @InjectMocks
    private DutyTimeConfigServiceImpl service;

    @Test
    void updateDutyTimeConfig_whenEnableToDisable_deletesSchedules()
    {
        DutyTimeConfig oldConfig = new DutyTimeConfig();
        oldConfig.setConfigId(1);
        oldConfig.setIsEnable(1);

        when(dutyTimeConfigMapper.selectDutyTimeConfigByConfigId(1)).thenReturn(oldConfig);
        when(dutyTimeConfigMapper.updateDutyTimeConfig(any(DutyTimeConfig.class))).thenReturn(1);

        DutyTimeConfig input = new DutyTimeConfig();
        input.setConfigId(1);
        input.setVenueId(2);
        input.setStartTime("08:00");
        input.setEndTime("09:00");
        input.setIsEnable(0);

        int row = service.updateDutyTimeConfig(input);

        verify(dutyScheduleMapper).deleteDutyScheduleByVenueAndTime(2, "08:00:00", "09:00:00");
        verify(dutyTimeConfigMapper).updateDutyTimeConfig(any(DutyTimeConfig.class));
        verifyNoMoreInteractions(dutyScheduleMapper);
        assertEquals(1, row);
    }

    @Test
    void updateDutyTimeConfig_whenEnableNull_doesNotThrow()
    {
        DutyTimeConfig oldConfig = new DutyTimeConfig();
        oldConfig.setConfigId(1);
        oldConfig.setIsEnable(1);

        when(dutyTimeConfigMapper.selectDutyTimeConfigByConfigId(1)).thenReturn(oldConfig);
        when(dutyTimeConfigMapper.updateDutyTimeConfig(any(DutyTimeConfig.class))).thenReturn(1);

        DutyTimeConfig input = new DutyTimeConfig();
        input.setConfigId(1);
        input.setVenueId(2);
        input.setStartTime("08:00");
        input.setEndTime("09:00");
        input.setIsEnable(null);

        int row = service.updateDutyTimeConfig(input);

        verify(dutyScheduleMapper, never()).deleteDutyScheduleByVenueAndTime(any(), any(), any());
        assertEquals(1, row);
    }

    @Test
    void insertDutyTimeConfig_formatsTimeAndSetsEnable()
    {
        when(dutyTimeConfigMapper.insertDutyTimeConfig(any(DutyTimeConfig.class))).thenReturn(1);

        DutyTimeConfig input = new DutyTimeConfig();
        input.setVenueId(2);
        input.setStartTime("08:00");
        input.setEndTime("09:00");

        int row = service.insertDutyTimeConfig(input);

        ArgumentCaptor<DutyTimeConfig> captor = ArgumentCaptor.forClass(DutyTimeConfig.class);
        verify(dutyTimeConfigMapper).insertDutyTimeConfig(captor.capture());
        DutyTimeConfig saved = captor.getValue();
        assertEquals(1, row);
        assertEquals(Integer.valueOf(1), saved.getIsEnable());
        assertEquals("08:00:00", saved.getStartTime());
        assertEquals("09:00:00", saved.getEndTime());
    }

    @Test
    void deleteDutyTimeConfigByConfigId_deletesSchedulesThenConfig()
    {
        DutyTimeConfig config = new DutyTimeConfig();
        config.setConfigId(1);
        config.setVenueId(2);
        config.setStartTime("08:00");
        config.setEndTime("09:00");

        when(dutyTimeConfigMapper.selectDutyTimeConfigByConfigId(1)).thenReturn(config);
        when(dutyTimeConfigMapper.deleteDutyTimeConfigByConfigId(1)).thenReturn(1);

        int row = service.deleteDutyTimeConfigByConfigId(1);

        verify(dutyScheduleMapper).deleteDutyScheduleByVenueAndTime(2, "08:00:00", "09:00:00");
        verify(dutyTimeConfigMapper).deleteDutyTimeConfigByConfigId(1);
        assertEquals(1, row);
    }

    @Test
    void deleteDutyTimeConfigByConfigIds_handlesNullArray()
    {
        int row = service.deleteDutyTimeConfigByConfigIds(null);
        verify(dutyTimeConfigMapper).deleteDutyTimeConfigByConfigIds(null);
        assertEquals(0, row);
    }

    @Test
    void deleteDutyTimeConfigByConfigIds_deletesSchedulesForEachConfig()
    {
        DutyTimeConfig config1 = new DutyTimeConfig();
        config1.setConfigId(1);
        config1.setVenueId(2);
        config1.setStartTime("08:00");
        config1.setEndTime("09:00");

        DutyTimeConfig config2 = new DutyTimeConfig();
        config2.setConfigId(2);
        config2.setVenueId(3);
        config2.setStartTime("10:00:00");
        config2.setEndTime("11:00:00");

        when(dutyTimeConfigMapper.selectDutyTimeConfigByConfigId(1)).thenReturn(config1);
        when(dutyTimeConfigMapper.selectDutyTimeConfigByConfigId(2)).thenReturn(config2);
        when(dutyTimeConfigMapper.deleteDutyTimeConfigByConfigIds(any(Integer[].class))).thenReturn(2);

        int row = service.deleteDutyTimeConfigByConfigIds(new Integer[] { 1, 2 });

        verify(dutyScheduleMapper).deleteDutyScheduleByVenueAndTime(2, "08:00:00", "09:00:00");
        verify(dutyScheduleMapper).deleteDutyScheduleByVenueAndTime(3, "10:00:00", "11:00:00");
        ArgumentCaptor<Integer[]> idsCaptor = ArgumentCaptor.forClass(Integer[].class);
        verify(dutyTimeConfigMapper).deleteDutyTimeConfigByConfigIds(idsCaptor.capture());
        assertArrayEquals(new Integer[] { 1, 2 }, idsCaptor.getValue());
        verify(dutyTimeConfigMapper, times(2)).selectDutyTimeConfigByConfigId(any());
        assertEquals(2, row);
    }

    @Test
    void selectDutyTimeConfigList_delegatesToMapper()
    {
        DutyTimeConfig query = new DutyTimeConfig();
        List<DutyTimeConfig> expected = Collections.singletonList(new DutyTimeConfig());
        when(dutyTimeConfigMapper.selectDutyTimeConfigList(query)).thenReturn(expected);

        List<DutyTimeConfig> result = service.selectDutyTimeConfigList(query);

        assertSame(expected, result);
    }

    @Test
    void selectDutyTimeConfigByVenueId_delegatesToMapper()
    {
        List<DutyTimeConfig> expected = Collections.singletonList(new DutyTimeConfig());
        when(dutyTimeConfigMapper.selectDutyTimeConfigByVenueId(2)).thenReturn(expected);

        List<DutyTimeConfig> result = service.selectDutyTimeConfigByVenueId(2);

        assertSame(expected, result);
    }

    @Test
    void selectDutyTimeConfigByConfigId_delegatesToMapper()
    {
        DutyTimeConfig expected = new DutyTimeConfig();
        when(dutyTimeConfigMapper.selectDutyTimeConfigByConfigId(1)).thenReturn(expected);

        DutyTimeConfig result = service.selectDutyTimeConfigByConfigId(1);

        assertSame(expected, result);
    }

    @Test
    void insertDutyTimeConfig_whenIsoWithoutZone_usesFallback()
    {
        when(dutyTimeConfigMapper.insertDutyTimeConfig(any(DutyTimeConfig.class))).thenReturn(1);

        DutyTimeConfig input = new DutyTimeConfig();
        input.setVenueId(2);
        input.setStartTime("2026-03-22T00:00:00.000");
        input.setEndTime("2026-03-22T12:34:56.000");

        service.insertDutyTimeConfig(input);

        ArgumentCaptor<DutyTimeConfig> captor = ArgumentCaptor.forClass(DutyTimeConfig.class);
        verify(dutyTimeConfigMapper).insertDutyTimeConfig(captor.capture());
        DutyTimeConfig saved = captor.getValue();
        assertEquals("00:00:00", saved.getStartTime());
        assertEquals("12:34:56", saved.getEndTime());
    }

    @Test
    void updateDutyTimeConfig_whenTimeHasSeconds_normalizesSeconds()
    {
        DutyTimeConfig oldConfig = new DutyTimeConfig();
        oldConfig.setConfigId(1);
        oldConfig.setIsEnable(1);

        when(dutyTimeConfigMapper.selectDutyTimeConfigByConfigId(1)).thenReturn(oldConfig);
        when(dutyTimeConfigMapper.updateDutyTimeConfig(any(DutyTimeConfig.class))).thenReturn(1);

        DutyTimeConfig input = new DutyTimeConfig();
        input.setConfigId(1);
        input.setVenueId(2);
        input.setStartTime("08:00:12");
        input.setEndTime("09:00:34");
        input.setIsEnable(1);

        service.updateDutyTimeConfig(input);

        ArgumentCaptor<DutyTimeConfig> captor = ArgumentCaptor.forClass(DutyTimeConfig.class);
        verify(dutyTimeConfigMapper).updateDutyTimeConfig(captor.capture());
        DutyTimeConfig updated = captor.getValue();
        assertEquals("08:00:00", updated.getStartTime());
        assertEquals("09:00:00", updated.getEndTime());
    }
}
