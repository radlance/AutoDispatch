package com.github.radlance.autodispatch.platform

interface PermissionController {

    fun askPermission()

    fun hasPermission(): Boolean
}