#include "main.h"
#include "buttonpanelex.h"
#include "CSettings.h"
#include "game/game.h"
#include "net/netgame.h"

extern CGUI *pGUI;
extern CSettings *pSettings;
extern CGame *pGame;
extern CNetGame *pNetGame;

RwTexture* m_pButtonExPassive = nullptr;
RwTexture* m_pButtonExActive = nullptr;

CButtonPanelEx::CButtonPanelEx() 
{
	Log(OBFUSCATE("[ButtonPanelEx]: Initializing.."));
	m_bIsActive = false;

    for(int i = 0; i < BUTTONPANELEX_COUNT; i++)
    {
       m_pButtonExPassive[i] = NULL;
       m_pButtonExActive[i] = NULL;
    }

    m_bPassengerEnable = true;
	if(m_bPassengerEnable)
	{
		m_pButtonExPassive[0] = (RwTexture*)LoadTextureFromDB(OBFUSCATE("samp"), OBFUSCATE("passenger"));
		if(m_pButtonExPassive[0]) ++m_pButtonExPassive[0]->refCount;
		m_pButtonExActive[0] = (RwTexture*)LoadTextureFromDB(OBFUSCATE("samp"), OBFUSCATE("passengerhover"));
		if(m_pButtonExActive[0]) ++m_pButtonExActive[0]->refCount;

		m_dwLastClickPassengerTick = GetTickCount();
	}

	SetPosition(230.0f, 500.0f);
    SetButtonSize(25.0f, 25.0f);
	Log(OBFUSCATE("[ButtonPanelEx]: Initialized."));
}

CButtonPanelEx::~CButtonPanelEx() 
{
	Log(OBFUSCATE("[~ButtonPanelEx]: Uninitilizing.."));

    for(int i = 0; i < BUTTONPANELEX_COUNT; i++)
    {
        if(m_pButtonExPassive[i]) 
		{
			CSprite2d_NEW::_Destructor(m_pButtonExPassive[i]);
			m_pButtonExPassive[i] = 0;
		}
        if(m_pButtonExActive[i]) 
		{
			CSprite2d_NEW::_Destructor(m_pButtonExActive[i]);
			m_pButtonExActive[i] = 0;
		}
    }

	Log(OBFUSCATE("[~ButtonPanelEx]: Uninitialized."));
}

void CButtonPanelEx::SetPosition(float posX, float posY)
{
	m_fPosX = pGUI->ScaleX(posX);
	m_fPosY = pGUI->ScaleY(posY);
	Log(OBFUSCATE("[ButtonPanelEx:SetPosition]: PosX: %f PosY: %f."), m_fPosX, m_fPosY);
}

void CButtonPanelEx::SetButtonSize(float sizeX, float sizeY)
{
	m_fSizeX = pGUI->ScaleX(sizeX);
	m_fSizeY = pGUI->ScaleX(sizeY);
	Log(OBFUSCATE("[ButtonPanelEx:SetButtonSize]: SizeX: %f SizeY: %f."), m_fSizeX, m_fSizeY);
}

void CButtonPanelEx::Render() 
{
	if(!m_bIsActive) return;

	CPlayerPed *pPlayerPed = pGame->FindPlayerPed();
	if(pPlayerPed) 
	{
		if(m_bPassengerEnable)
		{
            ImGuiIO &io = ImGui::GetIO();

			ImGui::PushStyleColor(ImGuiCol_Button, (ImVec4)ImColor(0x00, 0x00, 0x00, 0x00).Value);
			ImGui::PushStyleColor(ImGuiCol_ButtonHovered, (ImVec4)ImColor(0x00, 0x00, 0x00, 0x00).Value);
			ImGui::PushStyleColor(ImGuiCol_ButtonActive, (ImVec4)ImColor(0x00, 0x00, 0x00, 0x00).Value);
			
			ImGuiStyle style;
			style.FrameBorderSize = ImGui::GetStyle().FrameBorderSize;
			ImGui::GetStyle().FrameBorderSize = 0.0f;
			
			ImGui::Begin(OBFUSCATE("ButtonPanelEx"), nullptr, ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoBackground | ImGuiWindowFlags_NoMove | ImGuiWindowFlags_NoResize | ImGuiWindowFlags_NoScrollbar | ImGuiWindowFlags_NoSavedSettings);
			
			ImVec2 vecButSize = ImVec2(m_fSizeX * 5 + 5.0f, m_fSizeY * 5);
			
			CVehiclePool *pVehiclePool = pNetGame->GetVehiclePool();
			if(pVehiclePool) 
			{
				uint16_t sNearestVehicleID = pVehiclePool->FindNearestToLocalPlayerPed();
				CVehicle *pVehicle = pVehiclePool->GetAt(sNearestVehicleID);
				if(pVehicle)
				{
					if(pVehicle->GetDistanceFromLocalPlayerPed() < 4.0f) 
					{
						CPlayerPool *pPlayerPool = pNetGame->GetPlayerPool();
						if(pPlayerPool) 
						{
							CLocalPlayer *pLocalPlayer = pPlayerPool->GetLocalPlayer();
							if(pLocalPlayer)
							{
								if(!pLocalPlayer->IsSpectating() && !pPlayerPed->IsInVehicle())
								{
									if(ImGui::ImageButton(GetTickCount() - m_dwLastClickPassengerTick >= 150 ? (ImTextureID)m_pButtonExPassive[0]->raster : (ImTextureID)m_pButtonExActive[0]->raster, vecButSize))
									{
										m_dwLastClickPassengerTick = GetTickCount();
										pPlayerPed->EnterVehicle(pVehicle->m_dwGTAId, true);
										Log("SendEnterVehicleNotification out");
										pLocalPlayer->SendEnterVehicleNotification(sNearestVehicleID, true);
									}
								}
							}
						}
					}
				}
			}
			
			ImGui::SetWindowSize(ImVec2(-1, -1));
			
			ImGui::SetWindowPos(ImVec2(m_fPosX, m_fPosY));
			ImGui::End();
			
			ImGui::PopStyleColor(3);
			ImGui::GetStyle().FrameBorderSize = style.FrameBorderSize;
		}
	}
}