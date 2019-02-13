package me.grudzien.patryk.factory;

interface AbstractFactory<ReturnObject, ReturnObjectEnumType> {

	ReturnObject create(final ReturnObjectEnumType returnObjectEnumType);
}
