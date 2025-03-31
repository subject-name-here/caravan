package com.unicorns.invisible.caravan.save


expect fun saveData()
expect suspend fun loadGDSave(): Save?
expect suspend fun loadLocalSave(): Save?