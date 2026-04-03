#pragma once
#include "gui/gui.h"

#define BUTTONPANELEX_COUNT 2

class CButtonPanelEx
{
	friend class CGUI;
public:
	CButtonPanelEx();
	~CButtonPanelEx();

	void SetPosition(float posX, float posY);
	void SetButtonSize(float sizeX, float sizeY);

protected:
	void Render();

public:
	void Show(bool bShow) { m_bIsActive = bShow; }
	
public:
	bool        m_bPassengerEnable;
    uint32_t    m_dwLastClickPassengerTick;
	
private:
	bool 		m_bIsActive;

	float 		m_fPosX;
	float 		m_fPosY;
	float 		m_fSizeX;
	float 		m_fSizeY;

    RwTexture*   m_pButtonExPassive[BUTTONPANELEX_COUNT];
    RwTexture*   m_pButtonExActive[BUTTONPANELEX_COUNT];
};
