class CSprite2d_NEW
{
public:
    static void _Constructor(RwTexture *pTexture)
    {
        // CSprite2d::CSprite2d
        ((void (*)(RwTexture **))(g_libGTASA+0x551810+1))(&pTexture);
    }

    static void _Destructor(RwTexture *pTexture)
    {
        // CSprite2d::~CSprite2d
        ((void (*)(RwTexture **))(g_libGTASA+0x551838+1))(&pTexture);
    }

    static void Delete(RwTexture *pTexture)
    {
        // CSprite2d::Delete
        ((void (*)(RwTexture **))(g_libGTASA+0x551820+1))(&pTexture);
    }

    static RwTexture *SetTexture(const char *szTexture)
    {
        RwTexture *pTexture = 0;

        // CSprite2d::SetTexture
        ((void (*)(RwTexture **, const char *))(g_libGTASA+0x551854+1))(&pTexture, szTexture);
    
        return pTexture;
    }

    static void Draw(RwTexture *pTexture, RECT *rect, uint32_t dwColor, float *uv)
    {
        // CSprite2d::Draw
	    ((void (*)(RwTexture *, RECT *, uint32_t *, float, float, float, float, float, float, float, float))(g_libGTASA+0x5526CC+1))(pTexture, rect, &dwColor, uv[0], uv[1], uv[2], uv[3], uv[4], uv[5], uv[6], uv[7]);
    }
};