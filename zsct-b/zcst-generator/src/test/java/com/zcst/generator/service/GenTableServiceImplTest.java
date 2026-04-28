package com.zcst.generator.service;

import com.zcst.generator.mapper.GenTableMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenTableServiceImplTest
{
    @Mock
    private GenTableMapper genTableMapper;

    @InjectMocks
    private GenTableServiceImpl service;

    @Test
    void createTable_whenMapperReturnsZero_returnsTrue()
    {
        when(genTableMapper.createTable("sql")).thenReturn(0);
        assertTrue(service.createTable("sql"));
    }

    @Test
    void createTable_whenMapperReturnsOne_returnsTrue()
    {
        when(genTableMapper.createTable("sql")).thenReturn(1);
        assertTrue(service.createTable("sql"));
    }

    @Test
    void createTable_whenMapperReturnsNegative_returnsFalse()
    {
        when(genTableMapper.createTable("sql")).thenReturn(-1);
        assertFalse(service.createTable("sql"));
    }
}

