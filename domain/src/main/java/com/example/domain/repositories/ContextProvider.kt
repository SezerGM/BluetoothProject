package com.example.domain.repositories

import javax.naming.Context

interface ContextProvider{
    fun getContext(): Any?
}